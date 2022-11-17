package com.amondfarm.api.security.dto;

import javax.validation.constraints.NotNull;

import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {

	@NotNull
	private String accessToken;
	@NotNull
	private ProviderType providerType;
	private String username;
}
