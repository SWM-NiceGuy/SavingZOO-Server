package com.amondfarm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

	private final UserService userService;

	@GetMapping("")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<User> getUserInfo() {

		return ResponseEntity.ok(userService.getCurrentUser().get());
	}
}
