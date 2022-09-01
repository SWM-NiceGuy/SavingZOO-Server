package com.amondfarm.api.member.dto;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.enums.ProviderType;

import lombok.Data;

/**
 * WithdrawRequest DTO
 *
 * @since 2022-09-01
 * @author jwlee
 */

@Data
public class WithdrawRequest {

	private ProviderType provider;
	private String uid;
}
