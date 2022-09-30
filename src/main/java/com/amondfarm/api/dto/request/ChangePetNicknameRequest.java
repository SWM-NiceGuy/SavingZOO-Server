package com.amondfarm.api.dto.request;

import lombok.Getter;

@Getter
public class ChangePetNicknameRequest {
	private Long userPetId;
	private String nickname;
}
