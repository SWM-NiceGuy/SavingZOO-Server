package com.amondfarm.api.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Member;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.domain.enums.UserStatus;
import com.amondfarm.api.dto.SignUpRequest;
import com.amondfarm.api.dto.SignUpResponse;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.dto.WithdrawResponse;
import com.amondfarm.api.repository.UserRepository;
import com.amondfarm.api.security.dto.AppleLoginRequest;
import com.amondfarm.api.security.dto.LoginTokenResponse;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.amondfarm.api.security.dto.TokenStatusCodeDto;
import com.amondfarm.api.security.jwt.TokenProvider;
import com.amondfarm.api.security.util.AppleLoginUtil;
import com.amondfarm.api.security.util.KakaoLoginUtil;
import com.amondfarm.api.security.util.OAuthUtil;
import com.amondfarm.api.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final KakaoLoginUtil kakaoLoginUtil;
	private final AppleLoginUtil appleLoginUtil;
	private OAuthUtil oAuthUtil;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Transactional
	public TokenStatusCodeDto login(LoginTokenRequest loginTokenRequest) {
		Boolean isSignUp = false;
		// Provider에 따라 다른 LoginUtil Set 하고 정보 받아오기
		oAuthUtil = setLoginUtil(loginTokenRequest.getProviderType());

		LoginUserInfoDto loginUserInfoDto = oAuthUtil.requestUserInfo(loginTokenRequest)
			.orElseThrow(() -> new NoSuchElementException("Provider에게서 정보를 받아올 수 없습니다."));

		Optional<User> findUser = userRepository.findByProviderTypeAndLoginId(
			loginUserInfoDto.getProviderType(), loginUserInfoDto.getLoginId());
		// 정보 없으면 회원가입하기
		User user = findUser
			.orElseGet(() -> signUp(oAuthUtil.createEntity(loginUserInfoDto)));

		return makeJwtResponse(user.getId().toString(), isSignUp);
	}

	private User signUp(User user) {
		return userRepository.save(user);
	}

	private TokenStatusCodeDto makeJwtResponse(String userId, Boolean isSignup) {
		int statusCode = Response.SC_OK;
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userId, "password");

		// loadUserByUsername실행됨
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		if (isSignup) {
			statusCode = Response.SC_CREATED;
		}

		return new TokenStatusCodeDto(tokenProvider.createToken(authentication), statusCode);
	}

	private OAuthUtil setLoginUtil(ProviderType providerType) {
		if (ProviderType.KAKAO.equals(providerType)) {
			return kakaoLoginUtil;
		} else if (ProviderType.APPLE.equals(providerType)) {
			return appleLoginUtil;
		}
		throw new NoSuchElementException("주어진 Provider 정보가 없습니다.");

	}

	public Optional<User> getUserInfo() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(id -> userRepository.findById(Long.valueOf(id)));
	}

	@Transactional
	public TokenStatusCodeDto appleJoin(AppleLoginRequest request) {
		User user = request.toEntity();
		validateDuplicateMember(user);    // 중복회원 체크
		signUp(user);
		return makeJwtResponse(user.getId().toString(), true);
	}

	private void validateDuplicateMember(User user) {

		userRepository.findMember(user.getProviderType(), user.getLoginId(), UserStatus.ACTIVE)
			.ifPresent(m -> {
				throw new IllegalArgumentException("이미 존재하는 회원입니다.");
			});
	}

	@Transactional
	public WithdrawResponse withdraw(WithdrawRequest request) {
		User user = userRepository.findMember(request.getProvider(), request.getLoginId(), UserStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		user.changeStatus(UserStatus.WITHDRAWAL);
		return new WithdrawResponse("ok");
	}
}
