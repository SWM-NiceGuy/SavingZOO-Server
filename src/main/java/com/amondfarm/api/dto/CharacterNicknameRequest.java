package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CharacterNicknameRequest {
	private ProviderType provider;
	private String uid;
	private String nickname;
}
