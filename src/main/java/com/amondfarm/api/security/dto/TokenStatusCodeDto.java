package com.amondfarm.api.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenStatusCodeDto {

	private String jwt;
	private int statusCode;
}
