package com.amondfarm.api.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
import com.amondfarm.api.dto.admin.AdminUserInfo;
import com.amondfarm.api.dto.admin.AllUserInfoResponse;
import com.amondfarm.api.dto.request.WeeklyMissionCountRequest;
import com.amondfarm.api.dto.response.WeeklyMissionCountResponse;
import com.amondfarm.api.repository.UserMissionRepository;
import com.amondfarm.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminService {

	private final UserRepository userRepository;
	private final UserMissionRepository userMissionRepository;

	public AllUserInfoResponse getUserInfos() {
		List<User> allUsers = userRepository.findAll();

		List<AdminUserInfo> adminUserInfos = new ArrayList<>();

		for (User user : allUsers) {
			UserPet userPet = user.getUserPets().stream()
				.filter(up -> up.getPet().getAcquisitionCondition() == AcquisitionCondition.BETA)
				.findFirst().orElseThrow(() -> new NoSuchElementException("캐릭터가 없습니다."));

			List<UserMission> doMissions = user.getUserMissions().stream()
				.filter(um -> um.getMissionStatus() != MissionStatus.INCOMPLETE)
				.collect(Collectors.toList());

			adminUserInfos.add(
				AdminUserInfo.builder()
					.userId(user.getId())
					.registerDate(user.getCreatedAt())
					.isAllowPush(user.isAllowPush())
					.username(user.getLoginUsername())
					.providerType(user.getProviderType())
					.userStatus(user.getStatus())
					.petName(userPet.getNickname())
					.currentStage(userPet.getCurrentStage())
					.currentLevel(userPet.getCurrentLevel())
					.currentExp(userPet.getCurrentExp())
					.lastPlayDate(userPet.getPlayedAt())
					.totalDoMissions(doMissions.size())
					.build()
			);
		}

		return AllUserInfoResponse.builder()
			.totalUsers(allUsers.size())
			.userInfos(adminUserInfos)
			.build();
	}

	public WeeklyMissionCountResponse getWeeklyMissionAuthCount(WeeklyMissionCountRequest request) {
		// 받은 날짜부터 최근 7일 미션 인증 횟수 구하기
		// LocalDateTime end = request.getDate();
		LocalDateTime end = LocalDateTime.of(request.getDate(), LocalTime.of(23, 59, 59));
		LocalDateTime start = LocalDateTime.of(end.minusDays(6).toLocalDate(), LocalTime.of(0,0,0));

		System.out.println(end);
		System.out.println(start);

		int count = userMissionRepository.countByAccomplishedAtBetween(start, end);

		List<UserMission> byAccomplishedAtBetween = userMissionRepository.findByAccomplishedAtBetween(start, end);

		return WeeklyMissionCountResponse.builder()
			.missionCount(count)
			.build();
	}
}
