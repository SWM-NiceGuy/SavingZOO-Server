package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PetInfo {
	private Long petId;
	private String image;
	private String name;
	private String nickname;
	private int currentLevel;
	private int currentExp;
	private int maxExp;
	private Boolean isPlayReady;
	private long remainedPlayTime;

	@Builder
	public PetInfo(Long petId, String image, String name, String nickname, int currentLevel, int currentExp, int maxExp, boolean isPlayReady, long remainedPlayTime) {
		this.petId = petId;
		this.image = image;
		this.name = name;
		this.nickname = nickname;
		this.currentLevel = currentLevel;
		this.currentExp = currentExp;
		this.maxExp = maxExp;
		this.isPlayReady = isPlayReady;
		this.remainedPlayTime = remainedPlayTime;
	}
}
