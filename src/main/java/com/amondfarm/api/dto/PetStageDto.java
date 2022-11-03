package com.amondfarm.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetStageDto {
	private Boolean growState;
	private String description;
	private int level;
	private BigDecimal weight;
	private int height;
	private String silhouetteImageUrl;
	private Timestamp grownDate;
}
