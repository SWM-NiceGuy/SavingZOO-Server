package com.amondfarm.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDailyMissionInfo {
	private Long userId;
	private int todayDoMissions;
}
