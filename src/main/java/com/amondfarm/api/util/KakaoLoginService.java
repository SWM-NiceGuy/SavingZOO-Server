package com.amondfarm.api.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.UserInfoResponse;
import com.amondfarm.api.util.client.KakaoAuthClient;
import com.amondfarm.api.util.client.KakaoUserApiClient;
import com.amondfarm.api.util.dto.KakaoToken;
import com.amondfarm.api.util.dto.KakaoUnlink;
import com.amondfarm.api.util.dto.KakaoUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoLoginService implements OAuthService {

	@Value("${oauth2.kakao.user-api-url}")
	private String userApiUrl;

	@Value("${oauth2.kakao.auth-url}")
	private String authUrl;

	@Value("${oauth2.kakao.rest-api-key}")
	private String restapiKey;

	@Value("${oauth2.kakao.admin-key}")
	private String adminKey;

	@Value("${oauth2.kakao.redirect-url}")
	private String redirectUrl;

	private final KakaoAuthClient kakaoAuthClient;
	private final KakaoUserApiClient kakaoUserApiClient;

	@Override
	public Optional<UserInfoResponse> getUserInfo(LoginRequest loginRequest) {

		KakaoUserInfo userInfo = kakaoUserApiClient.getUserInfo("Bearer " + loginRequest.getAccessToken());
		return Optional.of(UserInfoResponse.builder()
			.loginId(userInfo.getId())
			.providerType(ProviderType.KAKAO)
			.nickname(userInfo.getKakaoAccount().getProfile().getNickname())
			.email(userInfo.getKakaoAccount().getEmail())
			.build());
	}

	public KakaoUserInfo getUserInfoTest(final String code) {
		final KakaoToken token = kakaoAuthClient.getToken(restapiKey, redirectUrl, code, "authorization_code");
		log.info("token = {}", token);
		return kakaoUserApiClient.getUserInfo(token.getTokenType() + " " + token.getAccessToken());
	}

	public void revoke(String targetId) {
		kakaoUserApiClient.unlinkUser("KakaoAK " + adminKey, "user_id", targetId);
	}
}
