package com.amondfarm.api.util;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amondfarm.api.dto.SlackDoMissionDto;
import com.slack.api.Slack;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.BlockElements;
import com.slack.api.model.block.element.ImageElement;
import com.slack.api.webhook.WebhookPayloads;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlackService {

	@Value(value = "${server-address.cdnUrl}")
	private String cdnUrl;

	@Value(value = "${slack.token}")
	private String token;
	@Value(value = "${slack.channel.monitor}")
	private String channel;

	@Value(value = "${slack.webhookUrl}")
	private String slackWebhookUrl;

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
			getHeader(slackDoMissionDto.getLoginUsername() + "님이 미션을 수행했어요!"),
			Blocks.divider(),
			getSection(
				"미션제목 : " + slackDoMissionDto.getMissionName() + "(" + slackDoMissionDto.getUserMissionId() + ")"),
			getSection("수행시각 : " + slackDoMissionDto.getAccomplishedAt()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
			Blocks.divider(),
			getSection("업로드한 이미지 : " + cdnUrl + slackDoMissionDto.getMissionImageUrl()),
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

	public String callbackApprove(BlockActionPayload blockPayload) {
		blockPayload.getMessage().getBlocks().remove(0);
		blockPayload.getActions().forEach(action -> {
			String value = action.getValue();

			if (action.getActionId().equals("action_reject")) {
				log.info("[complete] value: " + value);
				// 반려 시
				blockPayload.getMessage().getBlocks().add(0,
					section(section ->
						section.text(markdownText("배송팁 등록을 *거부* 하였습니다."))
					)
				);
				// dAppDeliveryTipService.updateDeliveryTip(seq, "N", userName);
			} else {
				log.info("[fail] value: " + value);
				blockPayload.getMessage().getBlocks().add(0,
					section(section ->
						section.text(markdownText("배송팁 등록을 *승인* 하였습니다."))
					)
				);
				// dAppDeliveryTipService.updateDeliveryTip(seq, "Y", userName);
			}
		});
		return null;
	}
}
