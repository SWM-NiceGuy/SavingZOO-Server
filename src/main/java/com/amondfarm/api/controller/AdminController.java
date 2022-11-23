package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.request.DateRequest;
import com.amondfarm.api.dto.admin.AllUserInfoResponse;
import com.amondfarm.api.dto.response.AverageMissionAccomplishResponse;
import com.amondfarm.api.dto.response.TotalRatingUpUserResponse;
import com.amondfarm.api.dto.response.WeeklyMissionCountResponse;
import com.amondfarm.api.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Slf4j
public class AdminController {

	private final AdminService adminService;

	// 전체 유저 정보 조회
	@GetMapping("/user/info")
	public ResponseEntity<AllUserInfoResponse> getUserInfos() {
		return ResponseEntity.ok(adminService.getUserInfos());
	}

	@GetMapping("/mission/weekly/auth-count")
	public ResponseEntity<WeeklyMissionCountResponse> getWeeklyMissionAuthCount(
		@RequestBody DateRequest request) {
		return ResponseEntity.ok(adminService.getWeeklyMissionAuthCount(request));
	}

	@GetMapping("/user/rating-up")
	public ResponseEntity<TotalRatingUpUserResponse> getRatingUpUser() {
		return ResponseEntity.ok(adminService.getRatingUpUser());
	}

	@GetMapping("/mission/accomplish/average")
	public ResponseEntity<AverageMissionAccomplishResponse> getAverageMissionAccomplish(
		@RequestBody DateRequest request) {
		return ResponseEntity.ok(adminService.getAverageMissionAccomplish(request));
	}
}
