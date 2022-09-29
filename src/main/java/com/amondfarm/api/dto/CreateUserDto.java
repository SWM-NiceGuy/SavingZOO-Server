package com.amondfarm.api.dto;

import java.util.List;

import com.amondfarm.api.domain.UserCharacter;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateUserDto {
	private ProviderType providerType;
	private String loginId;
	private String loginUsername;
	private String email;
	private List<UserMission> userMissions;
	private UserCharacter userCharacter;

	@Builder
	public CreateUserDto(ProviderType providerType, String loginId, String loginUsername, String email,
		List<UserMission> userMissions, UserCharacter userCharacter) {
		this.providerType = providerType;
		this.loginId = loginId;
		this.loginUsername = loginUsername;
		this.email = email;
		this.userMissions = userMissions;
		this.userCharacter = userCharacter;
	}
}
