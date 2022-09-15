package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MissionRequest {

	private ProviderType providerType;
	private String uid;
	private int mission;
}
