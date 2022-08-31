package com.amondfarm.api.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.dto.ExperienceRequest;
import com.amondfarm.api.member.dto.ExperienceResponse;
import com.amondfarm.api.member.dto.SigninRequest;
import com.amondfarm.api.member.dto.SigninResponse;
import com.amondfarm.api.member.enums.ProviderType;
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
	public ResponseEntity<SigninResponse> join(@RequestBody @Valid SigninRequest request) {
		SigninResponse response = memberService.join(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/v1/exp")
	public ResponseEntity<ExperienceResponse> getExperience(
		@RequestParam(value = "provider") ProviderType provider,
		@RequestParam(value = "uid") String uid) {

		ExperienceResponse response = memberService.getExperience(provider, uid);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/v1/exp")
	public ResponseEntity<ExperienceResponse> updateExperience(@RequestBody ExperienceRequest request) {
		ExperienceResponse response = memberService.updateExperience(request);
		return ResponseEntity.ok(response);
	}
}
