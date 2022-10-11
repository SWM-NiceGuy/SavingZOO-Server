package com.amondfarm.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.mission.RewardType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMissionDetailResponse {
	private String name;
	private String description;
	private String content;
	private String submitGuide;
	private List<String> exampleImageUrls;
	private RewardType rewardType;
	private int reward;
	private MissionStatus state;

	@Builder
	public UserMissionDetailResponse(String name, String description, String content, String submitGuide,
		List<String> exampleImageUrls, RewardType rewardType, int reward, MissionStatus state) {
		this.name = name;
		this.description = description;
		this.content = content;
		this.submitGuide = submitGuide;
		this.exampleImageUrls = exampleImageUrls;
		this.rewardType = rewardType;
		this.reward = reward;
		this.state = state;
	}
}
