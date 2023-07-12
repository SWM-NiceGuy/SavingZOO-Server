package com.amondfarm.api.dto.admin;

import java.time.LocalDateTime;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.domain.enums.user.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserInfo {
	// 유저 정보
	private Long userId;
	private LocalDateTime registerDate;
	private Boolean isAllowPush;
	private String username;
	private ProviderType providerType;
	private UserStatus userStatus;
	// 캐릭터 정보
	private String petName;
	private int currentStage;
	private int currentLevel;
	private int currentExp;
	private LocalDateTime lastPlayDate;
	// 수행한 미션 정보
	private int totalDoMissions;

	public static AdminUserInfo of(User user, UserPet userPet, int size) {
		return AdminUserInfo.builder()
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
			.totalDoMissions(size)
			.build();
	}
}
