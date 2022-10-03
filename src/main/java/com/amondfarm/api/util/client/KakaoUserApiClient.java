package com.amondfarm.api.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.amondfarm.api.config.FeignClientConfig;
import com.amondfarm.api.util.dto.KakaoToken;
import com.amondfarm.api.util.dto.KakaoUnlink;
import com.amondfarm.api.util.dto.KakaoUnlinkResponse;
import com.amondfarm.api.util.dto.KakaoUserInfo;

@FeignClient(name = "kakaoClient", url = "https://kapi.kakao.com", configuration = FeignClientConfig.class)
public interface KakaoUserApiClient {

	@PostMapping("/v2/user/me")
	KakaoUserInfo getUserInfo(@RequestHeader("Authorization") String accessToken);

	@PostMapping("/v1/user/unlink")
	KakaoUnlinkResponse unlinkUser(@RequestHeader("Authorization") String appAdminKey,
		@RequestParam("target_id_type") String targetIdType,
		@RequestParam("target_id") String targetId);
}
