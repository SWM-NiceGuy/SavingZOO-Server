package com.amondfarm.api.security.dto;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginUserInfoDto {

	private String loginId;
	private ProviderType providerType;
	private String nickname;
	private String email;
	private String profileImage;

	@Builder
	public LoginUserInfoDto(String loginId, ProviderType providerType, String nickname, String email,
		String profileImage) {
		this.loginId = loginId;
		this.providerType = providerType;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
	}
}
