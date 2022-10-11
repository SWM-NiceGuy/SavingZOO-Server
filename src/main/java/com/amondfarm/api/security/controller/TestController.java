package com.amondfarm.api.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class TestController {

	@GetMapping("")
	public String test() {
		return "Hello JWT 인증 성공 !";
	}
}
