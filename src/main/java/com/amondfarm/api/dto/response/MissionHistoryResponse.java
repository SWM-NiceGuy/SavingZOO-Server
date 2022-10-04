package com.amondfarm.api.dto.response;

import java.util.List;

import com.amondfarm.api.dto.MissionHistory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionHistoryResponse {

	private int totalMissionHistory;
	private List<MissionHistory> missionHistories;

	@Builder
	public MissionHistoryResponse(int totalMissionHistory, List<MissionHistory> missionHistories) {
		this.totalMissionHistory = totalMissionHistory;
		this.missionHistories = missionHistories;
	}
}
