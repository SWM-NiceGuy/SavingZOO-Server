package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChangePetNicknameResponse {
	private Long userPetId;
	private String nickname;

	@Builder
	public ChangePetNicknameResponse(Long userPetId, String nickname) {
		this.userPetId = userPetId;
		this.nickname = nickname;
	}
}
