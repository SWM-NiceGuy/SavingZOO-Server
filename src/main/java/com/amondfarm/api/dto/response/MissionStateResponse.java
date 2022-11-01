package com.amondfarm.api.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionStateResponse {
	private int totalCompletedMission;
	private List<CompletedMission> completedMissions;
	private int totalRejectedMission;
	private List<RejectedMission> rejectedMissions;
}
