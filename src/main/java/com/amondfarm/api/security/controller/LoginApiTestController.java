package com.amondfarm.api.security.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.amondfarm.api.security.util.KakaoLoginUtil;
import com.amondfarm.api.service.OAuthTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/login")
public class LoginApiTestController {

	private final OAuthTestService oAuthTestService;
	private final KakaoLoginUtil kakaoLoginUtil;

	@PostMapping("")
	public ResponseEntity<LoginUserInfoDto> login(@Valid @RequestBody LoginTokenRequest loginTokenRequest) {

		Optional<LoginUserInfoDto> loginUserInfoDto = kakaoLoginUtil.requestUserInfo(loginTokenRequest);

		return ResponseEntity.ok(loginUserInfoDto.orElse(null));
	}

	@GetMapping("/getkakaotoken")
	public void kakaoCallBack(@RequestParam String code) {
		System.out.println("[UserController] start");
		String access_token = "";
		access_token = oAuthTestService.getKakaoAccessToken(code);

		System.out.println("[UserController] end ------");
		System.out.println("[UserController] token : " + access_token);
	}

	@GetMapping("/logout")
	public String logout(@RequestParam String accessToken) {
		kakaoLoginUtil.logout(accessToken);
		return "success";
	}
}
