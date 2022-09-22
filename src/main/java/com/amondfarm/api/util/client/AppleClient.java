package com.amondfarm.api.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.amondfarm.api.config.FeignClientConfig;
import com.amondfarm.api.util.dto.ApplePublicKeyResponse;
import com.amondfarm.api.util.dto.AppleToken;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignClientConfig.class)
public interface AppleClient {
	@GetMapping(value = "/keys")
	ApplePublicKeyResponse getAppleAuthPublicKey();

	@PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
	AppleToken.Response getToken(AppleToken.Request request);

	@PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
	void revoke(AppleToken.RevokeRequest request);
}
