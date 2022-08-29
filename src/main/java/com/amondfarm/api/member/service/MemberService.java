package com.amondfarm.api.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.dto.SigninRequest;
import com.amondfarm.api.member.dto.SigninResponse;
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
public class MemberService {

	private final MemberRepository memberRepository;

	public SigninResponse join(SigninRequest request) {
		Member member = request.toEntity();
		validateDuplicateMember(member);
		memberRepository.save(request.toEntity());
		return new SigninResponse("ok");
	}

	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByEmail(member.getEmail());
		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}
}
