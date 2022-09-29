package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupDto {
	private ProviderType providerType;
	private String loginId;
	private String loginUsername;
	private String email;
}
