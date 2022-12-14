package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.response.BannerResponse;
import com.amondfarm.api.dto.response.CheckResponse;
import com.amondfarm.api.dto.response.NoticeResponse;
import com.amondfarm.api.service.UtilService;
import com.amondfarm.api.util.SlackService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UtilController {

	private final UtilService utilService;

	@GetMapping("/check/{clientVersion}")
	public ResponseEntity<CheckResponse> checkAppVersionAndMessage(@PathVariable String clientVersion) {

		return ResponseEntity.ok(utilService.checkVersion(clientVersion));
	}

	@GetMapping("/banners")
	public ResponseEntity<BannerResponse> getBanners() {
		return ResponseEntity.ok(utilService.getBanners());
	}

	@GetMapping("/notice")
	public ResponseEntity<NoticeResponse> getNotice() {
		return ResponseEntity.ok(utilService.getNotice());
	}
}
