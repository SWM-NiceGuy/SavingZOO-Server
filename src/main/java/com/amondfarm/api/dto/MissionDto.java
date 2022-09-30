package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.mission.MissionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionDto {
	private Long id;
	private String name;
	private String iconUrl;
	private MissionStatus state;

	@Builder
	public MissionDto(Long id, String name, String iconUrl, MissionStatus state) {
		this.id = id;
		this.name = name;
		this.iconUrl = iconUrl;
		this.state = state;
	}
}
