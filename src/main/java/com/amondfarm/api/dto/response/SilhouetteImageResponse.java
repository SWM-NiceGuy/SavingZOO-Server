package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SilhouetteImageResponse {
	private String stage2SilhouetteUrl;
	private String stage3SilhouetteUrl;
}
