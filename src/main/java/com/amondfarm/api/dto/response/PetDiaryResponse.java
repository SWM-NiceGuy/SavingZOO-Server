package com.amondfarm.api.dto.response;

import java.util.List;

import com.amondfarm.api.dto.PetStageDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetDiaryResponse {
	private String petName;
	private long birthday;
	private String species;
	private List<PetStageDto> stages;
}
