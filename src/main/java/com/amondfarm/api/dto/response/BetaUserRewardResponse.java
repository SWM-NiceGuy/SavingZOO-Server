package com.amondfarm.api.dto.response;

import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BetaUserRewardResponse {
	private Boolean isBetaUser;
	private RewardType rewardType;
	private int reward;
}
