package com.amondfarm.api.security.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityUtil {

	//Security Context 의 Authentication 객체를 이용해 username 을 리턴해주는 메소드
	public static Optional<String> getCurrentUsername() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Security Context에 Authentication 객체가 저장되는 시점은
		// JwtFilter의 doFilter 메소드에서 Request가 들어올 때

		if (authentication == null) {
			log.debug("Security Context에 인증 정보가 없습니다.");
			return Optional.empty();
			// throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
		}

		String username = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			username = springSecurityUser.getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			username = (String)authentication.getPrincipal();
		}

		return Optional.ofNullable(username);
	}
}
