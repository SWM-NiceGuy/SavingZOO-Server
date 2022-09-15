package com.amondfarm.api.dto;

import com.amondfarm.api.domain.enums.ProviderType;

import lombok.Data;

/**
 * WithdrawRequest DTO
 *
 * @since 2022-09-01
 * @author jwlee
 */

@Data
public class WithdrawRequest {

	private ProviderType providerType;
	private String uid;
}
