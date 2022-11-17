package com.amondfarm.api.dto;

import java.util.List;

import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateUserDto {
	private ProviderType providerType;
	private String loginId;
	private String accountUsername;
	private String loginUsername;
	private String email;
	private List<UserMission> userMissions;
	private UserPet userPet;

	@Builder
	public CreateUserDto(ProviderType providerType, String loginId, String accountUsername, String loginUsername, String email,
		List<UserMission> userMissions, UserPet userPet) {
		this.providerType = providerType;
		this.loginId = loginId;
		this.accountUsername = accountUsername;
		this.loginUsername = loginUsername;
		this.email = email;
		this.userMissions = userMissions;
		this.userPet = userPet;
	}
}
