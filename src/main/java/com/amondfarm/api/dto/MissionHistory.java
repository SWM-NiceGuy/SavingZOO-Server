package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionHistory {

	private Long missionHistoryId;
	private long date;
	private String title;
	private MissionStatus state;
	private RewardType rewardType;
	private int reward;

	@Builder
	public MissionHistory(Long missionHistoryId, long date, String title, MissionStatus state, RewardType rewardType, int reward) {
		this.missionHistoryId = missionHistoryId;
		this.date = date;
		this.title = title;
		this.state = state;
		this.rewardType = rewardType;
		this.reward = reward;
	}
}
