package com.amondfarm.api.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.dto.ExperienceRequest;
import com.amondfarm.api.member.dto.ExperienceResponse;
import com.amondfarm.api.member.dto.SigninRequest;
import com.amondfarm.api.member.dto.SigninResponse;
import com.amondfarm.api.member.enums.ProviderType;
import com.amondfarm.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * Member Service
 *
 * @since 2022-08-29
 * @author jwlee
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public SigninResponse join(SigninRequest request) {
		Member member = request.toEntity();
		validateDuplicateMember(member);	// 중복회원 체크
		memberRepository.save(request.toEntity());
		return new SigninResponse("ok");
	}

	public ExperienceResponse getExperience(ProviderType provider, String email) {

		Member member = memberRepository.findByProviderAndEmail(provider, email)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		return new ExperienceResponse(member.getExp());
	}

	@Transactional
	public ExperienceResponse updateExperience(ExperienceRequest request) {

		Member member = memberRepository.findByProviderAndEmail(request.getProvider(), request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		member.changeExp(request.getExp());
		return new ExperienceResponse(member.getExp());
	}

	private void validateDuplicateMember(Member member) {

		memberRepository.findByProviderAndEmail(member.getProvider(), member.getEmail())
			.ifPresent(m -> {
				throw new IllegalArgumentException("이미 존재하는 회원입니다. 회원 Mail : " + m.getEmail());
			});
	}
}
