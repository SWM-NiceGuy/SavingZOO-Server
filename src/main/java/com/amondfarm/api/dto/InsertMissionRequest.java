package com.amondfarm.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InsertMissionRequest {
	private boolean admin;
	private List<InsertMissionDto> missions = new ArrayList<>();
}
