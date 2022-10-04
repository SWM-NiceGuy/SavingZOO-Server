package com.amondfarm.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amondfarm.api.dto.request.DeviceToken;
import com.amondfarm.api.dto.request.ChangePetNicknameRequest;
import com.amondfarm.api.dto.response.ChangePetNicknameResponse;
import com.amondfarm.api.dto.response.DailyMissionsResponse;
import com.amondfarm.api.dto.response.InitPetResponse;
import com.amondfarm.api.dto.response.MissionHistoryResponse;
import com.amondfarm.api.dto.response.UserMissionDetailResponse;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/pet/info")
	public ResponseEntity<InitPetResponse> getInitInfo() {
		InitPetResponse userPetInfo = userService.getUserPetInfo();
		return ResponseEntity.ok(userPetInfo);

	}

	@PostMapping("/pet/nickname")
	public ResponseEntity<ChangePetNicknameResponse> setPetNickname(
		@RequestBody ChangePetNicknameRequest changePetNicknameRequest) {
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

	@PostMapping(value = "/mission/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
		MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> get(@PathVariable Long id, @RequestPart MultipartFile multipartFile) {

		String originalFilename = multipartFile.getOriginalFilename();
		userService.doMission(id, multipartFile);

		return ResponseEntity.ok(originalFilename);
	}

	@GetMapping("/mission/history")
	public ResponseEntity<MissionHistoryResponse> getMissionHistory() {
		return ResponseEntity.ok(userService.getMissionHistory());
	}

	@PostMapping("/device/token")
	public ResponseEntity<DeviceToken> saveDeviceToken(@RequestBody DeviceToken request) {
		userService.setDeviceToken(request);
		return ResponseEntity.ok(request);
	}
}
