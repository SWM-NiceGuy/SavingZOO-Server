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
				"*??????(??????ID)*\n" + slackDoMissionDto.getLoginUsername() + "(" + slackDoMissionDto.getUserId() + ")"));
			textObjects.add(markdownText("*????????????*\n" + slackDoMissionDto.getAccomplishedAt()));
			textObjects.add(markdownText(
				"*????????????(????????????ID)*\n" + slackDoMissionDto.getMissionName() + "(" + slackDoMissionDto.getUserMissionId()
					+ ")"));
			textObjects.add(markdownText("*?????? ?????? ??????*\n" + slackDoMissionDto.getMissionImageUrl()));

			MethodsClient methods = Slack.getInstance().methods(token);
			ChatPostMessageRequest request = ChatPostMessageRequest.builder()
				.channel(channel)
				.blocks(asBlocks(
					header(
						header -> header.text(plainText(slackDoMissionDto.getLoginUsername() + "?????? ?????? ?????? ????????? ???????????????!"))),
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
			getHeader(slackDoMissionDto.getLoginUsername() + "??? (" + slackDoMissionDto.getUserId().toString()
				+ ")??? ????????? ???????????????!"),
			Blocks.divider(),
			getSection(
				"*????????????* " + slackDoMissionDto.getMissionName() + " (" + slackDoMissionDto.getUserMissionId().toString()
					+ ")"),
			getSection("*????????????* : " + slackDoMissionDto.getAccomplishedAt()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
			Blocks.divider(),
			getSection(slackDoMissionDto.getMissionImageUrl()),
			Blocks.actions(getActionBlocks(slackDoMissionDto.getMissionImageUrl()))
		);

		try {
			Slack.getInstance().send(slackWebhookUrl, WebhookPayloads
				.payload(
					p -> p.text(slackDoMissionDto.getLoginUsername() + "??? (" + slackDoMissionDto.getUserId().toString()
							+ ")??? ????????? ???????????????!")
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
		actions.add(getActionButton("??????", imageUrl, "primary", "action_approve"));
		actions.add(getActionButton("????????? ?????? ?????? ??????", "fail", "danger", "action_reject"));
		actions.add(getActionButton("?????? ?????? ??????", "fail", "danger", "action_reject_2"));
		actions.add(getActionButton("????????? ??????", "fail", "danger", "action_reject_3"));
		return actions;
	}

	@Transactional
	public String callbackApprove(BlockActionPayload blockPayload) {

		String actionId = blockPayload.getActions().get(0).getActionId();

		if (actionId.equals("action_approve")) {
			// ??????
			log.info(
				"[approve] user mission image : " + blockPayload.getMessage().getAttachments().get(0).getImageUrl());
			approveMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl());

			// ?????? ?????? ?????? ?????????
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*?????? ??????*"))));
			blockPayload.getActions().remove(0);

		} else if (actionId.equals("action_reject")) {
			rejectMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl(), "????????? ?????? ?????? ??????");

			// ?????? ?????? ?????? ?????????
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*?????? ??????*"))));
			blockPayload.getActions().remove(0);
		} else if (actionId.equals("action_reject_2")) {
			rejectMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl(), "?????? ?????? ??????");

			// ?????? ?????? ?????? ?????????
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*?????? ??????*"))));
			blockPayload.getActions().remove(0);
		} else if (actionId.equals("action_reject_3")) {
			rejectMission(blockPayload.getMessage().getAttachments().get(0).getImageUrl(), "????????? ??????");

			// ?????? ?????? ?????? ?????????
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*?????? ??????*"))));
			blockPayload.getActions().remove(0);
		} else {
			log.error("Slack Callback Error");
			blockPayload.getMessage().getBlocks().add(1,
				section(section -> section.text(markdownText("*?????? ??????. ?????? ??????????????? ???????????????!*"))));
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
		// ?????? ID ??? ???????????? ?????? ?????? ?????? ??????

		UserMission userMission = userMissionRepository.findBySubmissionImageUrl(imageUrl)
			.orElseThrow(() -> new NoSuchElementException("?????? ???????????? ????????????."));

		// ?????? ?????? ??????
		userMission.approveMission(LocalDateTime.now());

		// User ?????? Push Notification ?????????
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null && userMission.getUser().isAllowPush()) {
			fcmService.sendMessageTo(deviceToken, "?????? ?????? ??????", "???????????? ????????? ??????????????????. ????????? ??????????????????!");
			userMission.sendNotification();
		}
	}

	private void rejectMission(String imageUrl, String reason) {
		UserMission userMission = userMissionRepository.findBySubmissionImageUrl(imageUrl)
			.orElseThrow(() -> new NoSuchElementException("?????? ???????????? ????????????."));

		// ?????? ?????? ??????
		userMission.rejectMission(LocalDateTime.now(), reason);

		// User ?????? Push Notification ?????????
		String deviceToken = userMission.getUser().getDeviceToken();
		if (deviceToken != null && userMission.getUser().isAllowPush()) {
			fcmService.sendMessageTo(deviceToken, "?????? ?????? ??????", "???????????? ????????? ?????? ??????????????????.");
			userMission.sendNotification();
		}
	}
}
