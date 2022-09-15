package com.amondfarm.api.security.dto;

import javax.validation.constraints.NotNull;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginTokenRequest {

	@NotNull
	private String accessToken;
	@NotNull
	private ProviderType providerType;
}
