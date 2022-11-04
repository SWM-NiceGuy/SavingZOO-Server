package com.amondfarm.api.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class MissionCheckRequest {
	private List<Long> missions;
}
