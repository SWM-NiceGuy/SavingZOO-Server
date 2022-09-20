package com.amondfarm.api.security.controller;

import javax.validation.Valid;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.dto.WithdrawResponse;
import com.amondfarm.api.security.dto.AppleLoginRequest;
import com.amondfarm.api.security.dto.LoginTokenResponse;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.TokenStatusCodeDto;
import com.amondfarm.api.security.util.KakaoLoginUtil;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginApiController {

	private final UserService userService;
	private final KakaoLoginUtil kakaoLoginUtil;

	@PostMapping("/login")
	public ResponseEntity<LoginTokenResponse> login(@Valid @RequestBody LoginTokenRequest loginTokenRequest) {
		TokenStatusCodeDto tokenStatusCodeDto = userService.login(loginTokenRequest);
		LoginTokenResponse loginTokenResponse = new LoginTokenResponse(tokenStatusCodeDto.getJwt());
		if (tokenStatusCodeDto.getStatusCode() == Response.SC_CREATED) {
			return ResponseEntity.status(Response.SC_CREATED)
				.body(loginTokenResponse);
		}
		return ResponseEntity.ok(loginTokenResponse);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<WithdrawResponse> withdraw(@RequestBody @Valid WithdrawRequest request) {
		WithdrawResponse response = userService.withdraw(request);
		return ResponseEntity.ok(response);
	}
}
