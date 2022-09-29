package com.amondfarm.api.dto.request.admin;

import com.amondfarm.api.domain.enums.mission.MissionType;
import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMissionRequest {
	private String title;
	private String description;
	private String reasonForMission;
	private MissionType missionType;
	private RewardType rewardType;
	private int reward;
	private String profileImageUrl;
}
