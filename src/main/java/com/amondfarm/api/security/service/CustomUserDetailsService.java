package com.amondfarm.api.security.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userId) {
		User user = userRepository.findById(Long.valueOf(userId))
			.orElseThrow(() -> new UsernameNotFoundException(userId + "DB에 존재하지 않는 사용자 입니다"));
		return createSecurityUser(userId, user);
	}

	private org.springframework.security.core.userdetails.User createSecurityUser(String userId, User user) {
		if (!user.isActivate()) {
			throw new RuntimeException(userId + " 활성 상태가 아닙니다");
		}
		return new org.springframework.security.core.userdetails.User(
			userId, passwordEncoder.encode("password"),
			Collections.singleton(new SimpleGrantedAuthority(user.getRoleType().getCode())));
	}

}
