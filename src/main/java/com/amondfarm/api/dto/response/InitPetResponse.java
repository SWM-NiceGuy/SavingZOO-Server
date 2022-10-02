package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InitPetResponse {
	private Long petId;
	private String image;
	private String name;
	private String nickname;
	private int currentLevel;
	private int currentExp;
	private int maxExp;

	@Builder
	public InitPetResponse(Long petId, String image, String name, String nickname, int currentLevel, int currentExp, int maxExp) {
		this.petId = petId;
		this.image = image;
		this.name = name;
		this.nickname = nickname;
		this.currentLevel = currentLevel;
		this.currentExp = currentExp;
		this.maxExp = maxExp;
	}
}
