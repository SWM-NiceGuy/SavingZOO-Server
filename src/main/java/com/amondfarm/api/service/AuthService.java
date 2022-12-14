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

import com.amondfarm.api.domain.Pet;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.domain.enums.user.UserStatus;
import com.amondfarm.api.dto.LoginTokenStatusDto;
import com.amondfarm.api.dto.MessageResponse;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.repository.PetRepository;
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

	private final UserService userService;
	private final UserRepository userRepository;
	private final PetRepository petRepository;

	private final KakaoLoginService kakaoLoginService;
	private final AppleLoginService appleLoginService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Transactional
	public LoginTokenStatusDto login(LoginRequest loginRequest) {

		// ProviderType ??? ?????? LoginUtil Set
		OAuthService oAuthService = setLoginUtil(loginRequest.getProviderType());

		// ?????? OAuth API ??????????????? ?????? ?????? ????????????
		UserInfoResponse userInfoResponse = oAuthService.getUserInfo(loginRequest)
			.orElseThrow(() -> new NoSuchElementException("Provider????????? ????????? ????????? ??? ????????????."));

		// ?????? ????????? ?????? ???????????? ???????????? ??????
		Optional<User> findUser = userRepository.findMember(
			userInfoResponse.getProviderType(), userInfoResponse.getLoginId(), UserStatus.ACTIVE);

		User user = findUser
			.orElseGet(() -> userService.joinUser(userInfoResponse));

		// default ???????????? ????????? ?????? ??? ????????? ?????????
		checkDefaultPet(user);

		String jwt = createJwt(user.getId().toString());
		if (findUser.isEmpty()) {
			return new LoginTokenStatusDto(jwt, Response.SC_CREATED);
		}
		return new LoginTokenStatusDto(jwt, Response.SC_OK);
	}

	private void checkDefaultPet(User user) {

		Optional<UserPet> userExistDefaultPet = user.getUserPets().stream()
			.filter(up -> up.getPet().getAcquisitionCondition() == AcquisitionCondition.DEFAULT)
			.findAny();

		// ?????? DEFAULT ?????? ????????? ??????
		if (userExistDefaultPet.isPresent()) {
			return;
		}

		// DEFAULT ????????? ??????
		Pet pet = petRepository.findByAcquisitionCondition(AcquisitionCondition.DEFAULT)
			.orElseThrow(() -> new NoSuchElementException("DEFAULT ???????????? ????????????."));

		// UserPet ??????
		user.addUserPet(UserPet.builder()
			.pet(pet)
			.build());
	}

	private String createJwt(String userId) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userId, "password");

		// loadUserByUsername ??????
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
		throw new NoSuchElementException("????????? Provider ????????? ????????????.");

	}

	@Transactional
	public MessageResponse withdraw(WithdrawRequest request) {

		if (request.getProviderType().equals(ProviderType.KAKAO)) {
			// ???????????? ???????????? ??????
			User user = getCurrentUser()
				.orElseThrow(() -> new NoSuchElementException("?????? ????????? ????????????."));
			kakaoLoginService.revoke(user.getLoginId());
			// ????????? DB ??? ??????
			user.changeStatus(UserStatus.WITHDRAWAL);

		} else if (request.getProviderType().equals(ProviderType.APPLE)) {

			try {
				// ????????? ???????????? ??????
				appleLoginService.revoke(request);
				// ????????? DB ??? ??????
				User user = getCurrentUser()
					.orElseThrow(() -> new NoSuchElementException("?????? ????????? ????????????."));
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
