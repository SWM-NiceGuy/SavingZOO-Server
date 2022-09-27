package com.amondfarm.api.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.dto.LoginTokenStatusDto;
import com.amondfarm.api.dto.MessageResponse;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.LoginTokenResponse;
import com.amondfarm.api.service.LoginService;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginApiController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<LoginTokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

		LoginTokenStatusDto tokenStatusDto = loginService.login(loginRequest);
		LoginTokenResponse loginTokenResponse = new LoginTokenResponse(tokenStatusDto.getJwt());

		return ResponseEntity.status(tokenStatusDto.getStatusCode())
			.body(loginTokenResponse);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<MessageResponse> withdraw(@RequestBody @Valid WithdrawRequest request) {
		MessageResponse response = loginService.withdraw(request);
		return ResponseEntity.ok(response);
	}
}
