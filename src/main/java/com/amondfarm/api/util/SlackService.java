package com.amondfarm.api.util;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.PetLevelValue;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
import com.amondfarm.api.dto.SlackDoMissionDto;
import com.amondfarm.api.repository.PetLevelRepository;
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
	private final PetLevelRepository petLevelRepository;

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
				.payload(p -> p.text("사용자가 미션을 수행했어요. 인증해주세요!")
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
		// userPet 경험치 상승
		UserPet userPet = userMission.getUser().getUserPets().stream()
			.filter(up -> up.getPet().getAcquisitionCondition() == AcquisitionCondition.BETA)
			.findFirst().orElseThrow(() -> new NoSuchElementException("해당 유저에게 BETA 캐릭터가 존재하지 않습니다."));

		if (userPet.getCurrentLevel() <= 10) {
			PetLevelValue petLevelValue = petLevelRepository.findByLevel(userPet.getCurrentLevel())
				.orElseThrow(() -> new NoSuchElementException("잘못된 레벨 정보입니다."));

			int afterExp = userPet.getCurrentExp() + userMission.getMission().getReward();
			if (afterExp >= petLevelValue.getMaxExp()) {    // 경험치가 현재 레벨 Max 값 이상. 레벨업 로직 수행
				userPet.changeLevel(userPet.getCurrentLevel() + 1);
				userPet.changeExp(afterExp - petLevelValue.getMaxExp());
				if (userPet.getCurrentLevel() == 10) {
					userPet.changeExp(160);
				}
				// 만약 레벨이 진화 조건에 해당하는 레벨이라면 해당 조건 단계로 changeStage
				int stage = userPet.getPet().checkStage(userPet.getCurrentLevel());
				if (stage != 0) {
					userPet.changeStage(stage);
				}
			} else {    // 경험치가 현재 레벨 Max 값보다 작음. 레벨은 그대로, 경험치만 상승
				userPet.changeExp(afterExp);
			}
		}

		// TODO User 에게 Push Notification 보내기
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null && userMission.getUser().isAllowPush()) {
			fcmService.sendMessageTo(deviceToken, "미션 인증 완료", "수행하신 미션이 인증되었어요. 눌러서 확인해보세요!");
			userMission.sendNotification();
		}
	}

	private void rejectMission(String imageUrl) {
		UserMission userMission = userMissionRepository.findBySubmissionImageUrl(imageUrl)
			.orElseThrow(() -> new NoSuchElementException("해당 이미지가 없습니다."));

		// 미션 성공 처리
		userMission.rejectMission(LocalDateTime.now(), "잘못된 사진입니다.");

		// TODO User 에게 Push Notification 보내기
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null && userMission.getUser().isAllowPush()) {
			fcmService.sendMessageTo(deviceToken, "미션 인증 반려", "수행하신 미션이 반려 처리되었어요.");
			userMission.sendNotification();
		}
	}
}
