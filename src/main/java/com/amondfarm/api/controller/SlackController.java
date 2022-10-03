package com.amondfarm.api.controller;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.util.SlackService;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.bolt.response.Response;
import com.slack.api.util.json.GsonFactory;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/slack")
public class SlackController {

	private final SlackService slackService;

	@PostMapping(
		value = "/callback",
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String callback(@RequestParam String payload) throws IOException {
		BlockActionPayload blockPayload = GsonFactory.createSnakeCase()
			.fromJson(payload, BlockActionPayload.class);

		return slackService.callbackApprove(blockPayload);
	}
}
