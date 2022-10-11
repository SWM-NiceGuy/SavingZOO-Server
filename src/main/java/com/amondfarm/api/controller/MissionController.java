package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.dto.request.admin.CreateMissionRequest;
import com.amondfarm.api.service.MissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mission")
public class MissionController {

	private final MissionService missionService;

	@PostMapping("")
	public ResponseEntity<Mission> createMission(@RequestBody CreateMissionRequest request) {
		return ResponseEntity.ok(missionService.createMission(request));
	}
}
