package com.amondfarm.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerDto {
	private String imageUrl;
	private String contentUrl;
}
