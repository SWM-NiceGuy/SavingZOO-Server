package com.amondfarm.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CheckResponse {
	private boolean required;
	private String latestVersion;
	private String releaseNote;

	@Builder
	public CheckResponse(boolean required, String latestVersion, String releaseNote) {
		this.required = required;
		this.latestVersion = latestVersion;
		this.releaseNote = releaseNote;
	}
}
