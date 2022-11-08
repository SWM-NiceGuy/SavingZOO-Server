package com.amondfarm.api.dto.response;

import java.util.List;

import com.amondfarm.api.dto.BannerDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerResponse {
	private int totalBanners;
	private List<BannerDto> banners;
}
