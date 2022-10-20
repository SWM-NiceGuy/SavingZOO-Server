package com.amondfarm.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PetPlayingInfo {
	private boolean isPlayReady;
	private long remainedPlayTime;

	@Builder
	public PetPlayingInfo(boolean isPlayReady, long remainedPlayTime) {
		this.isPlayReady = isPlayReady;
		this.remainedPlayTime = remainedPlayTime;
	}
}
