package com.amondfarm.api.service;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.domain.enums.UserStatus;
import com.amondfarm.api.dto.LoginTokenStatusDto;
import com.amondfarm.api.dto.MessageResponse;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.repository.UserRepository;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.amondfarm.api.security.jwt.TokenProvider;
import com.amondfarm.api.security.util.SecurityUtil;
import com.amondfarm.api.util.AppleLoginUtil;
import com.amondfarm.api.util.KakaoLoginUtil;
import com.amondfarm.api.util.OAuthUtil;

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
	public LoginTokenStatusDto login(LoginRequest loginRequest) {

		// ProviderType 에 따라 LoginUtil Set
		oAuthUtil = setLoginUtil(loginRequest.getProviderType());

		// 외부 OAuth API 서버로부터 유저 정보 받아오기
		LoginUserInfoDto loginUserInfoDto = oAuthUtil.getUserInfo(loginRequest)
			.orElseThrow(() -> new NoSuchElementException("Provider에게서 정보를 받아올 수 없습니다."));

		// 해당 유저가 우리 서비스의 유저인지 찾기
		Optional<User> findUser = userRepository.findByProviderTypeAndLoginId(
			loginUserInfoDto.getProviderType(), loginUserInfoDto.getLoginId());

		User user = findUser
			.orElseGet(() -> signUp(oAuthUtil.createEntity(loginUserInfoDto)));

		String jwt = createJwt(user.getId().toString());

		// 정보 없으면 회원가입하기
		if (findUser.isEmpty()) {
			return new LoginTokenStatusDto(jwt, Response.SC_CREATED);
		}
		return new LoginTokenStatusDto(jwt, Response.SC_OK);
	}

	private User signUp(User user) {
		return userRepository.save(user);
	}

	private String createJwt(String userId) {
		int statusCode = Response.SC_OK;
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userId, "password");

		// loadUserByUsername 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return tokenProvider.createToken(authentication);
	}

	private OAuthUtil setLoginUtil(ProviderType providerType) {
		if (ProviderType.KAKAO.equals(providerType)) {
			return kakaoLoginUtil;
		} else if (ProviderType.APPLE.equals(providerType)) {
			return appleLoginUtil;
		}
		throw new NoSuchElementException("주어진 Provider 정보가 없습니다.");

	}

	public Optional<User> getCurrentUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(id -> userRepository.findById(Long.valueOf(id)));
	}

	private void validateDuplicateMember(User user) {

		userRepository.findMember(user.getProviderType(), user.getLoginId(), UserStatus.ACTIVE)
			.ifPresent(m -> {
				throw new IllegalArgumentException("이미 존재하는 회원입니다.");
			});
	}

	@Transactional
	public MessageResponse withdraw(WithdrawRequest request) {

		if (request.getProviderType().equals(ProviderType.KAKAO)) {
			// 카카오에 회원탈퇴 요청
			// TODO

			// 서비스 DB 에 반영
			User user = getCurrentUser()
				.orElseThrow(() -> new NoSuchElementException("해당 회원이 없습니다."));
			user.changeStatus(UserStatus.WITHDRAWAL);

		} else if (request.getProviderType().equals(ProviderType.APPLE)) {

			try {
				// 애플에 회원탈퇴 요청
				appleLoginUtil.revoke(request);
				// 서비스 DB 에 반영
				User user = getCurrentUser()
					.orElseThrow(() -> new NoSuchElementException("해당 회원이 없습니다."));
				user.changeStatus(UserStatus.WITHDRAWAL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new MessageResponse("ok");
	}
}
