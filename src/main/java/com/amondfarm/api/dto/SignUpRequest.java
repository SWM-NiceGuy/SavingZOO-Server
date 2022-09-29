package com.amondfarm.api.dto;

import com.amondfarm.api.domain.old.Member;
import com.amondfarm.api.domain.enums.user.Gender;
import com.amondfarm.api.domain.enums.old.MemberStatus;
import com.amondfarm.api.domain.enums.user.ProviderType;

import lombok.Data;

/**
 * SignInRequest DTO
 *
 * @since 2022-08-29
 * @author jwlee
 */


@Data
public class SignUpRequest {

	private ProviderType provider;
	private String uid;
	private String email;
	private String nickname;
	private Gender gender;
	private int ageGroup;

	public Member toEntity() {
		return Member.builder()
			.provider(provider)
			.uid(uid)
			.email(email)
			.nickname(nickname)
			.status(MemberStatus.ACTIVE)
			.gender(gender)
			.ageGroup(ageGroup)
			.build();
	}
}