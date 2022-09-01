package com.amondfarm.api.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.dto.ExperienceRequest;
import com.amondfarm.api.member.dto.ExperienceResponse;
import com.amondfarm.api.member.dto.SignUpRequest;
import com.amondfarm.api.member.dto.SignUpResponse;
import com.amondfarm.api.member.dto.WithdrawRequest;
import com.amondfarm.api.member.dto.WithdrawResponse;
import com.amondfarm.api.member.enums.MemberStatus;
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
	public SignUpResponse join(SignUpRequest request) {
		Member member = request.toEntity();
		validateDuplicateMember(member);	// 중복회원 체크
		memberRepository.save(member);
		return new SignUpResponse("ok");
	}

	@Transactional
	public WithdrawResponse withdraw(WithdrawRequest request) {
		Member member = memberRepository.findMember(request.getProvider(), request.getUid(), MemberStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		member.changeStatus(MemberStatus.WITHDRAWAL);
		return new WithdrawResponse("ok");
	}

	public ExperienceResponse getExperience(ProviderType provider, String uid) {

		Member member = memberRepository.findMember(provider, uid, MemberStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		return new ExperienceResponse(member.getExp());
	}

	@Transactional
	public ExperienceResponse updateExperience(ExperienceRequest request) {

		Member member = memberRepository.findMember(request.getProvider(), request.getUid(), MemberStatus.ACTIVE)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

		member.changeExp(request.getExp());
		return new ExperienceResponse(member.getExp());
	}

	private void validateDuplicateMember(Member member) {

		memberRepository.findMember(member.getProvider(), member.getUid(), MemberStatus.ACTIVE)
			.ifPresent(m -> {
				throw new IllegalArgumentException("이미 존재하는 회원입니다.");
			});
	}
}
