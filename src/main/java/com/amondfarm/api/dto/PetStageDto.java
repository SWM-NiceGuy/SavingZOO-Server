package com.amondfarm.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetStageDto {
	private int stage;
	private Boolean growState;
	private String imageUrl;
	private String description;
	private int level;
	private String weight;
	private String height;
	private long grownDate;
}
