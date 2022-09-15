package com.amondfarm.api.security.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppleLoginUtil implements OAuthUtil {
	@Override
	public Optional<LoginUserInfoDto> requestUserInfo(LoginTokenRequest token) {
		return Optional.empty();
	}

	@Override
	public User createEntity(LoginUserInfoDto loginUserInfoDto) {
		return null;
	}
}
