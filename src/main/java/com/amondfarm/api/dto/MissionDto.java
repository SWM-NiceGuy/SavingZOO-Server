package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionDto {
	private Long id;
	private String name;
	private String category;
	private String iconUrl;
	private MissionStatus state;
	private RewardType rewardType;
	private int reward;

	@Builder
	public MissionDto(Long id, String name, String category, String iconUrl, MissionStatus state, RewardType rewardType, int reward) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.iconUrl = iconUrl;
		this.state = state;
		this.rewardType = rewardType;
		this.reward = reward;
	}
}
