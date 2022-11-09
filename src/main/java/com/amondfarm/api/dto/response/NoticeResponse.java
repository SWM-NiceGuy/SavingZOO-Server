package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {
	private Boolean isApply;
	private Boolean isRequired;
	private String message;
}
