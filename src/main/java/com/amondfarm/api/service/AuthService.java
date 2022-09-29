package com.amondfarm.api.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.enums.mission.MissionType;
import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.domain.enums.user.UserStatus;
import com.amondfarm.api.dto.CreateUserDto;
import com.amondfarm.api.dto.LoginTokenStatusDto;
import com.amondfarm.api.dto.MessageResponse;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.repository.MissionRepository;
import com.amondfarm.api.repository.UserRepository;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.UserInfoResponse;
import com.amondfarm.api.security.jwt.TokenProvider;
import com.amondfarm.api.security.util.SecurityUtil;
import com.amondfarm.api.util.AppleLoginService;
import com.amondfarm.api.util.KakaoLoginService;
import com.amondfarm.api.util.OAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final MissionRepository missionRepository;
	private final KakaoLoginService kakaoLoginService;
	private final AppleLoginService appleLoginService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Transactional
	public LoginTokenStatusDto login(LoginRequest loginRequest) {

		// ProviderType 에 따라 LoginUtil Set
		OAuthService oAuthService = setLoginUtil(loginRequest.getProviderType());

		// 외부 OAuth API 서버로부터 유저 정보 받아오기
		UserInfoResponse userInfoResponse = oAuthService.getUserInfo(loginRequest)
			.orElseThrow(() -> new NoSuchElementException("Provider에게서 정보를 받아올 수 없습니다."));

		// 해당 유저가 우리 서비스의 유저인지 찾기
		Optional<User> findUser = userRepository.findByProviderTypeAndLoginId(
			userInfoResponse.getProviderType(), userInfoResponse.getLoginId());

		User user = findUser
			.orElseGet(() -> joinUser(userInfoResponse));

		String jwt = createJwt(user.getId().toString());

		// 정보 없으면 회원가입하기
		if (findUser.isEmpty()) {
			return new LoginTokenStatusDto(jwt, Response.SC_CREATED);
		}
		return new LoginTokenStatusDto(jwt, Response.SC_OK);
	}

	private User joinUser(UserInfoResponse userInfoResponse) {

		// 데일리 미션 찾기
		List<Mission> dailyMissions = missionRepository.findAllMissionsByMissionType(MissionType.DAILY);
		List<UserMission> userMissions = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrow = now.plusDays(1);

		for (Mission mission : dailyMissions) {
			// 오늘 날짜의 미션 삽입
			userMissions.add(new UserMission(mission, now));
			// 다음 날짜의 미션 미리 삽입
			userMissions.add(new UserMission(mission, tomorrow));
		}

		// TODO 기본 캐릭터 찾기 repository

		// TODO UserCharacter 생성 후 DTO 에 삽입

		CreateUserDto userDto = CreateUserDto.builder()
			.loginId(userInfoResponse.getLoginId())
			.providerType(userInfoResponse.getProviderType())
			.loginUsername(userInfoResponse.getNickname())
			.email(userInfoResponse.getEmail())
			.userMissions(userMissions)
			.build();

		 return userRepository.save(User.from(userDto));
	}

	private String createJwt(String userId) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userId, "password");

		// loadUserByUsername 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return tokenProvider.createToken(authentication);
	}

	private OAuthService setLoginUtil(ProviderType providerType) {
		if (ProviderType.KAKAO.equals(providerType)) {
			return kakaoLoginService;
		} else if (ProviderType.APPLE.equals(providerType)) {
			return appleLoginService;
		}
		throw new NoSuchElementException("주어진 Provider 정보가 없습니다.");

	}

	@Transactional
	public MessageResponse withdraw(WithdrawRequest request) {

		if (request.getProviderType().equals(ProviderType.KAKAO)) {
			// 카카오에 회원탈퇴 요청
			User user = getCurrentUser()
				.orElseThrow(() -> new NoSuchElementException("해당 회원이 없습니다."));
			kakaoLoginService.revoke(user.getLoginId());
			// 서비스 DB 에 반영
			user.changeStatus(UserStatus.WITHDRAWAL);

		} else if (request.getProviderType().equals(ProviderType.APPLE)) {

			try {
				// 애플에 회원탈퇴 요청
				appleLoginService.revoke(request);
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

	private Optional<User> getCurrentUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(id -> userRepository.findById(Long.valueOf(id)));
	}
}
