package com.amondfarm.api.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.amondfarm.api.config.FeignClientConfig;
import com.amondfarm.api.util.dto.KakaoToken;

@FeignClient(name = "kakaoAuthClient", url = "https://kauth.kakao.com", configuration = FeignClientConfig.class)
public interface KakaoAuthClient {

	@PostMapping(value = "/oauth/token")
	KakaoToken getToken(@RequestParam("client_id") String restApiKey,
		@RequestParam("redirect_uri") String redirectUrl,
		@RequestParam("code") String code,
		@RequestParam("grant_type") String grantType);
}
