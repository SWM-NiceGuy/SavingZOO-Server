package com.amondfarm.api.dto.request.admin;

import java.util.List;

import com.amondfarm.api.domain.enums.mission.MissionType;
import com.amondfarm.api.domain.enums.mission.RewardType;
import com.amondfarm.api.dto.request.MissionExampleImageDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMissionRequest {
	private String title;
	private String description;
	private String submitGuide;
	private MissionType missionType;
	private RewardType rewardType;
	private int reward;
	private String profileImageUrl;

	private List<MissionExampleImageDto> exampleImages;
}
