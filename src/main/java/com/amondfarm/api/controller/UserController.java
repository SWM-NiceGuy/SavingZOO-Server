package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.response.DailyMissionsResponse;
import com.amondfarm.api.dto.request.ChangePetNicknameRequest;
import com.amondfarm.api.dto.response.ChangePetNicknameResponse;
import com.amondfarm.api.dto.response.InitPetResponse;
import com.amondfarm.api.dto.response.UserMissionDetailResponse;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

	private final UserService userService;

	// 앱 실행 시 초기 캐릭터 정보 조회 API
	@GetMapping("/pet/info")
	public ResponseEntity getInitInfo() {
		InitPetResponse userPetInfo = userService.getUserPetInfo();
		return ResponseEntity.ok(userPetInfo);

	}

	@PostMapping("/pet/nickname")
	public ResponseEntity<ChangePetNicknameResponse> setPetNickname(@RequestBody ChangePetNicknameRequest changePetNicknameRequest) {
		return ResponseEntity.ok(userService.setUserPetNickname(changePetNicknameRequest));
	}

	@GetMapping("/mission/daily")
	public ResponseEntity<DailyMissionsResponse> getDailyMissions() {
		return ResponseEntity.ok(userService.getDailyMissions());
	}

	@GetMapping("/mission/{id}")
	public ResponseEntity<UserMissionDetailResponse> getUserMissionDetail(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserMissionDetail(id));
	}

	// @GetMapping("")
	// @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	// public ResponseEntity<User> getUserInfo() {
	// 	return null;
	// 	// return ResponseEntity.ok(userService.getCurrentUser().get());
	// }
}
