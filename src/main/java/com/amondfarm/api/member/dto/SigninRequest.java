package com.amondfarm.api.member.dto;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.enums.Gender;
import com.amondfarm.api.member.enums.ProviderType;

import lombok.Builder;
import lombok.Data;

/**
 * SignInRequest DTO
 *
 * @since 2022-08-29
 * @author jwlee
 */


@Data
public class SigninRequest {

	private ProviderType provider;
	private String uid;
	private String email;
	private String nickname;
	private Gender gender;
	private int ageGroup;

	@Builder
	public SigninRequest(ProviderType provider, String uid, String email, String nickname, Gender gender, int ageGroup) {
		this.provider = provider;
		this.uid = uid;
		this.email = email;
		this.nickname = nickname;
		this.gender = gender;
		this.ageGroup = ageGroup;
	}


	public Member toEntity() {
		return Member.builder()
			.provider(provider)
			.uid(uid)
			.email(email)
			.nickname(nickname)
			.gender(gender)
			.ageGroup(ageGroup)
			.build();
	}
}
