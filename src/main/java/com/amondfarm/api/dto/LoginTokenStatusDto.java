package com.amondfarm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginTokenStatusDto {

	private String jwt;
	private int statusCode;
}
