package com.amondfarm.api.util;

import java.util.Optional;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;

public interface OAuthUtil {

	Optional<LoginUserInfoDto> getUserInfo(LoginRequest token);
	User createEntity(LoginUserInfoDto loginUserInfoDto);
}
