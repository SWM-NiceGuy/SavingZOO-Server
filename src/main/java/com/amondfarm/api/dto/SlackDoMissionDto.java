package com.amondfarm.api.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SlackDoMissionDto {
	private Long userId;
	private String loginUsername;
	private LocalDateTime accomplishedAt;
	private Long userMissionId;
	private String missionName;
	private String missionImageUrl;

	@Builder
	public SlackDoMissionDto(Long userId, String loginUsername, LocalDateTime accomplishedAt, Long userMissionId, String missionName, String missionImageUrl) {
		this.userId = userId;
		this.loginUsername = loginUsername;
		this.accomplishedAt = accomplishedAt;
		this.userMissionId = userMissionId;
		this.missionName = missionName;
		this.missionImageUrl = missionImageUrl;
	}
}
