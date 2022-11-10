package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.response.BetaUserRewardResponse;
import com.amondfarm.api.service.BetaUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/beta")
public class BetaUserController {

	private final BetaUserService betaUserService;

	@GetMapping("/check")
	public ResponseEntity<BetaUserRewardResponse> getBetaUserReward() {
		return ResponseEntity.ok(betaUserService.getBetaUserReward());
	}
}
