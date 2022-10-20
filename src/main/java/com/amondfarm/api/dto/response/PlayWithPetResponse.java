package com.amondfarm.api.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayWithPetResponse {
	private Boolean isSuccess;
	private String msg;

	private PetInfo petInfo;

	@Builder
	public PlayWithPetResponse(boolean isSuccess, String msg, PetInfo petInfo) {
		this.isSuccess = isSuccess;
		this.msg = msg;
		this.petInfo = petInfo;
	}
}
