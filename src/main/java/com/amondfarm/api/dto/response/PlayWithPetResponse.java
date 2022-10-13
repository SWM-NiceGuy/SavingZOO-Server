package com.amondfarm.api.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayWithPetResponse {
	private Boolean isSuccess;
	private String msg;
	private Long petId;
	private String image;
	private String name;
	private String nickname;
	private int currentLevel;
	private int currentExp;
	private int maxExp;
	private long lastPlayedAt;

	@Builder
	public PlayWithPetResponse(boolean isSuccess, String msg, Long petId, String image, String name, String nickname, int currentLevel,
		int currentExp, int maxExp, long lastPlayedAt) {
		this.isSuccess = isSuccess;
		this.msg = msg;
		this.petId = petId;
		this.image = image;
		this.name = name;
		this.nickname = nickname;
		this.currentLevel = currentLevel;
		this.currentExp = currentExp;
		this.maxExp = maxExp;
		this.lastPlayedAt = lastPlayedAt;
	}
}
