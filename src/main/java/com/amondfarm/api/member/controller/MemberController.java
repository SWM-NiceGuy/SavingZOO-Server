package com.amondfarm.api.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.dto.SigninRequest;
import com.amondfarm.api.member.dto.SigninResponse;
import com.amondfarm.api.member.service.MemberService;

import lombok.RequiredArgsConstructor;

/**
 * Member Controller
 *
 * @since 2022-08-29
 * @author jwlee
 */


@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/v1/signin")
	public ResponseEntity join(@RequestBody @Valid SigninRequest request) {
		SigninResponse response = memberService.join(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
