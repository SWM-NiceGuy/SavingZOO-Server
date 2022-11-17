package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectedMission {
	private String missionTitle;
	private String reason;
}
