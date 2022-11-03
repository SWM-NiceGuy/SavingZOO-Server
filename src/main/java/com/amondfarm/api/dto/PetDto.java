package com.amondfarm.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetDto {
	private PetStageDto stage1;
	private PetStageDto stage2;
	private PetStageDto stage3;
}
