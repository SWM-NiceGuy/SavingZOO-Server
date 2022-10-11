package com.amondfarm.api.security.dto;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleLoginRequest {

	private ProviderType providerType;
	private String uid;
	private String email;
	private String nickname;

	public User toEntity() {
		return User.builder()
			.providerType(providerType)
			.loginId(uid)
			.email(email)
			.loginUsername(nickname)
			.build();
	}
}
