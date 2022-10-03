package com.amondfarm.api.util;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.dto.SlackDoMissionDto;
import com.amondfarm.api.repository.UserMissionRepository;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.ActionResponseSender;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.response.ActionResponse;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.BlockElements;
import com.slack.api.webhook.WebhookPayloads;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackService {

	@Value(value = "${server-address.cdnUrl}")
	private String cdnUrl;

	@Value(value = "${slack.token}")
	private String token;
	@Value(value = "${slack.channel.monitor}")
	private String channel;

	@Value(value = "${slack.webhookUrl}")
	private String slackWebhookUrl;

	private final FCMService fcmService;
	private final UserMissionRepository userMissionRepository;

	public void postSlackUserMissionMessage(SlackDoMissionDto slackDoMissionDto) {

		try {
			List<TextObject> textObjects = new ArrayList<>();
			textObjects.add(markdownText(
				"*이름(유저ID)*\n" + slackDoMissionDto.getLoginUsername() + "(" + slackDoMissionDto.getUserId() + ")"));
			textObjects.add(markdownText("*수행시각*\n" + slackDoMissionDto.getAccomplishedAt()));
			textObjects.add(markdownText(
				"*미션제목(유저미션ID)*\n" + slackDoMissionDto.getMissionName() + "(" + slackDoMissionDto.getUserMissionId()
					+ ")"));
			textObjects.add(markdownText("*미션 수행 사진*\n" + slackDoMissionDto.getMissionImageUrl()));

			MethodsClient methods = Slack.getInstance().methods(token);
			ChatPostMessageRequest request = ChatPostMessageRequest.builder()
				.channel(channel)
				.blocks(asBlocks(
					header(
						header -> header.text(plainText(slackDoMissionDto.getLoginUsername() + "님이 미션 수행 사진을 올렸습니다!"))),
					divider(),
					section(section -> section.fields(textObjects)
					))).build();

			methods.chatPostMessage(request);
		} catch (SlackApiException | IOException e) {
			log.error(e.getMessage());
		}
	}

	public void postSlack(SlackDoMissionDto slackDoMissionDto) {
		List<LayoutBlock> layoutBlocks = Blocks.asBlocks(
			getHeader(slackDoMissionDto.getLoginUsername() + "님 (" + slackDoMissionDto.getUserId().toString()
				+ ")이 미션을 수행했어요!"),
			Blocks.divider(),
			getSection(
				"*미션제목* " + slackDoMissionDto.getMissionName() + " (" + slackDoMissionDto.getUserMissionId().toString()
					+ ")"),
			getSection("*수행시각* : " + slackDoMissionDto.getAccomplishedAt()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
			Blocks.divider(),
			getSection(slackDoMissionDto.getMissionImageUrl()),
			Blocks.actions(getActionBlocks(slackDoMissionDto.getMissionImageUrl()))
		);

		try {
			Slack.getInstance().send(slackWebhookUrl, WebhookPayloads
				.payload(p -> p.text("Slack 에 메시지를 출력하지 못했습니다.")
					.blocks(layoutBlocks)));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private LayoutBlock getHeader(String text) {
		return Blocks.header(h -> h.text(
			BlockCompositions.plainText(pt -> pt.emoji(true)
				.text(text))));
	}

	private LayoutBlock getSection(String message) {
		return Blocks.section(s -> s.text(
			BlockCompositions.markdownText(message)));
	}

	private LayoutBlock getImageSection(String imageUrl) {
		return Blocks.image(imageBlockBuilder -> imageBlockBuilder.imageUrl(imageUrl));
	}

	private BlockElement getActionButton(String plainText, String value, String style, String actionId) {
		return BlockElements.button(b -> b.text(plainText(plainText, true))
			.value(value)
			.style(style)
			.actionId(actionId));
	}

	private List<BlockElement> getActionBlocks(String imageUrl) {
		List<BlockElement> actions = new ArrayList<>();
		actions.add(getActionButton("인증", imageUrl, "primary", "action_approve"));
		actions.add(getActionButton("반려", "fail", "danger", "action_reject"));
		return actions;
	}

	@Transactional
	public String callbackApprove(BlockActionPayload blockPayload) {

		String actionId = blockPayload.getActions().get(0).getActionId();

		if (actionId.equals("action_approve")) {
			// 인증
			log.info(
				"[approve] user mission image : " + blockPayload.getMessage().getAttachments().get(0).getImageUrl());
			approveMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl());

			// 인증 처리 완료 메시지
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*승인 완료*"))));
			blockPayload.getActions().remove(0);

		} else if (actionId.equals("action_reject")) {
			// 반려
			log.info(
				"[reject] user mission image : " + blockPayload.getMessage().getAttachments().get(0).getImageUrl());
			rejectMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl());

			// 반려 처리 완료 메시지
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*반려 처리*"))));
			blockPayload.getActions().remove(0);
		} else {
			log.error("Slack Callback Error");
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*오류 발생. 서버 관리자에게 문의하세요!*"))));
		}

		ActionResponse response = ActionResponse.builder()
			.replaceOriginal(true)
			.blocks(blockPayload.getMessage().getBlocks())
			.build();

		try {
			ActionResponseSender sender = new ActionResponseSender(Slack.getInstance());
			sender.send(blockPayload.getResponseUrl(), response);
		} catch (IOException e) {
			log.error("IOException");
		}

		return null;
	}

	private void approveMission(String imageUrl) {
		// 해당 ID 에 해당하는 유저 미션 성공 처리

		UserMission userMission = userMissionRepository.findBySubmissionImageUrl(imageUrl)
			.orElseThrow(() -> new NoSuchElementException("해당 이미지가 없습니다."));

		// 미션 성공 처리
		userMission.approveMission(LocalDateTime.now());

		// TODO User 에게 Push Notification 보내기
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null) {
			try {
				fcmService.sendMessageTo(deviceToken, "미션 인증 완료", "수행하신 미션이 인증되었어요. 보상을 받아가세요!");
			} catch (IOException e) {
				log.error("FCM 메시지를 보내는 데에 실패했습니다.");
			}
		}
	}

	private void rejectMission(String imageUrl) {
		UserMission userMission = userMissionRepository.findBySubmissionImageUrl(imageUrl)
			.orElseThrow(() -> new NoSuchElementException("해당 이미지가 없습니다."));

		// 미션 성공 처리
		userMission.rejectMission(LocalDateTime.now(), "잘못된 사진입니다.");

		// TODO User 에게 Push Notification 보내기
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null) {
			try {
				fcmService.sendMessageTo(deviceToken, "미션 인증 반려", "수행하신 미션이 반려 처리되었어요.");
			} catch (IOException e) {
				log.error("FCM 메시지를 보내는 데에 실패했습니다.");
			}
		}
	}
}
