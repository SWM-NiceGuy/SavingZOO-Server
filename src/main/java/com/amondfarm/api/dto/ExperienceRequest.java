package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExperienceRequest {

	private ProviderType provider;
	private String uid;
	private int exp;
}
