package com.amondfarm.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserNameRewardResponse {

	private String username;
	private int rewardQuantity;
}
