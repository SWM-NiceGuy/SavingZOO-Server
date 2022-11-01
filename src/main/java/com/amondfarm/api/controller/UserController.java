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

import com.amondfarm.api.domain.enums.PushType;
import com.amondfarm.api.dto.AllowPushState;
import com.amondfarm.api.dto.request.MissionCheckRequest;
import com.amondfarm.api.dto.request.PlayWithPetRequest;
import com.amondfarm.api.dto.request.DeviceToken;
import com.amondfarm.api.dto.request.ChangePetNicknameRequest;
import com.amondfarm.api.dto.response.ChangePetNicknameResponse;
import com.amondfarm.api.dto.response.MissionStateResponse;
import com.amondfarm.api.dto.response.DailyMissionsResponse;
import com.amondfarm.api.dto.response.PetInfo;
import com.amondfarm.api.dto.response.MissionHistoryResponse;
import com.amondfarm.api.dto.response.PlayWithPetResponse;
import com.amondfarm.api.dto.response.RewardResponse;
import com.amondfarm.api.dto.response.UserMissionDetailResponse;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/user")
public class UserController {

	private final UserService userService;

	@GetMapping("/pet/info")
	public ResponseEntity<PetInfo> getInitInfo() {
		return ResponseEntity.ok(userService.getUserPetInfo());

	}

	@PostMapping("/pet/nickname")
	public ResponseEntity<ChangePetNicknameResponse> setPetNickname(
		@RequestBody ChangePetNicknameRequest changePetNicknameRequest) {
		return ResponseEntity.ok(userService.setUserPetNickname(changePetNicknameRequest));
	}

	@PostMapping("/pet/play")
	public ResponseEntity<PlayWithPetResponse> playWithPet(@RequestBody PlayWithPetRequest playWithPetRequest) {
		return ResponseEntity.ok(userService.playWithPet(playWithPetRequest));
	}

	@GetMapping("/pet/feed")
	public ResponseEntity<RewardResponse> feedPet() {
		return ResponseEntity.ok(userService.feedPet());
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

		log.info("user mission id : " + id);
		String originalFilename = multipartFile.getOriginalFilename();
		log.info("multipart file name : " + originalFilename);
		userService.doMission(id, multipartFile);

		return ResponseEntity.ok(originalFilename);
	}

	@GetMapping("/mission/history")
	public ResponseEntity<MissionHistoryResponse> getMissionHistory() {
		return ResponseEntity.ok(userService.getMissionHistory());
	}

	@GetMapping("/mission/check")
	public ResponseEntity<MissionStateResponse> getMissionState() {
		return ResponseEntity.ok(userService.getMissionState());
	}

	@PostMapping("/mission/reward")
	public ResponseEntity<RewardResponse> getReward(@RequestBody MissionCheckRequest request) {
		return ResponseEntity.ok(userService.getReward(request));

	}

	@PostMapping("/device/token")
	public ResponseEntity<DeviceToken> saveDeviceToken(@RequestBody DeviceToken request) {
		userService.setDeviceToken(request);
		return ResponseEntity.ok(request);
	}

	@PostMapping("/device/push")
	public ResponseEntity<AllowPushState> changeAllowPushState(@RequestBody AllowPushState request) {
		return ResponseEntity.ok(userService.setAllowPushState(PushType.MISSION, request.isAllowPush()));
	}
}
