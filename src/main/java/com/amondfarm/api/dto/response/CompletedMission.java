package com.amondfarm.api.dto.response;

import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompletedMission {
	private Long missionId;
	private String missionTitle;
	private RewardType rewardType;
	private int reward;
}