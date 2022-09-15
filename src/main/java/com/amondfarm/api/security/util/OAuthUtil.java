package com.amondfarm.api.security.util;

import java.util.Optional;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;

public interface OAuthUtil {

	Optional<LoginUserInfoDto> requestUserInfo(LoginTokenRequest token);
	User createEntity(LoginUserInfoDto loginUserInfoDto);
}
