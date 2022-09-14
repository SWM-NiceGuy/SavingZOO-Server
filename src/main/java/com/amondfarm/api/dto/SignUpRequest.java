package com.amondfarm.api.dto;

import com.amondfarm.api.domain.Member;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.Gender;
import com.amondfarm.api.domain.enums.MemberStatus;
import com.amondfarm.api.domain.enums.UserStatus;
import com.amondfarm.api.domain.enums.ProviderType;

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