package com.amondfarm.api.util;

import java.util.Optional;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.UserInfoResponse;

public interface OAuthService {

	Optional<UserInfoResponse> getUserInfo(LoginRequest token);
}
