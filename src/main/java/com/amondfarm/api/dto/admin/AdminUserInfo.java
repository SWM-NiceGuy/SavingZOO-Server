package com.amondfarm.api.dto.admin;

import java.time.LocalDateTime;

import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
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
	private AcquisitionCondition petKind;
	private String petName;
	private int currentStage;
	private int currentLevel;
	private int currentExp;
	private LocalDateTime lastPlayDate;
	// 수행한 미션 정보
	private int totalDoMissions;
}
