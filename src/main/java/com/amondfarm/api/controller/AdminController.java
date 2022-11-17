package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.admin.AllUserInfoResponse;
import com.amondfarm.api.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

	private final AdminService adminService;

	// 전체 유저 정보 조회
	@GetMapping("/user/info")
	public ResponseEntity<AllUserInfoResponse> getUserInfos() {
		return ResponseEntity.ok(adminService.getUserInfos());
	}
}
