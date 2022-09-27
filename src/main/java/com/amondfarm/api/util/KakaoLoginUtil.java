package com.amondfarm.api.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.amondfarm.api.security.util.SecurityUtil;
import com.amondfarm.api.util.client.KakaoAuthClient;
import com.amondfarm.api.util.client.KakaoUserApiClient;
import com.amondfarm.api.util.dto.KakaoToken;
import com.amondfarm.api.util.dto.KakaoUnlink;
import com.amondfarm.api.util.dto.KakaoUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginUtil implements OAuthUtil {

	@Value("${oauth2.kakao.user-api-url}")
	private String userApiUrl;

	@Value("${oauth2.kakao.auth-url}")
	private String authUrl;

	@Value("${oauth2.kakao.restapi-key}")
	private String restapiKey;

	@Value("${oauth2.kakao.redirect-url}")
	private String redirectUrl;

	private final KakaoAuthClient kakaoAuthClient;
	private final KakaoUserApiClient kakaoUserApiClient;

	@Override
	public User createEntity(LoginUserInfoDto loginUserInfoDto) {
		return User.from(loginUserInfoDto);
	}

	@Override
	public Optional<LoginUserInfoDto> getUserInfo(LoginRequest loginRequest) {

		KakaoUserInfo userInfo = kakaoUserApiClient.getUserInfo("Bearer " + loginRequest.getAccessToken());
		return Optional.of(LoginUserInfoDto.builder()
			.loginId(userInfo.getId())
			.providerType(ProviderType.KAKAO)
			.email(userInfo.getKakaoAccount().getEmail())
			.build());
	}

	public KakaoUserInfo getUserInfoTest(final String code) {
		final KakaoToken token = kakaoAuthClient.getToken(restapiKey, redirectUrl, code, "authorization_code");
		log.info("token = {}", token);
		return kakaoUserApiClient.getUserInfo(token.getTokenType() + " " + token.getAccessToken());
	}

	public void revoke(String targetId) {

		KakaoUnlink kakaoUnlink = new KakaoUnlink(targetId);
		kakaoUserApiClient.unlinkUser(restapiKey, kakaoUnlink);
	}
}
