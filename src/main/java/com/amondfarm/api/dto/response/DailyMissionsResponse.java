package com.amondfarm.api.dto.response;

import java.util.List;

import com.amondfarm.api.dto.MissionDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DailyMissionsResponse {
	private int totalMissions;
	private List<MissionDto> missions;

	@Builder
	public DailyMissionsResponse(int totalMissions, List<MissionDto> missions) {
		this.totalMissions = totalMissions;
		this.missions = missions;
	}
}
