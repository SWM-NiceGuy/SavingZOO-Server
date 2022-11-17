package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyMissionCountResponse {
	int missionCount;
}
