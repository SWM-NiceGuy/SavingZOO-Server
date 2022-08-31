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
	private String email;
	private String nickname;
	private Gender gender;
	private int age;

	@Builder
	public SigninRequest(ProviderType provider, String email, String nickname, Gender gender, int age) {
		this.provider = provider;
		this.email = email;
		this.nickname = nickname;
		this.gender = gender;
		this.age = age;
	}

	public Member toEntity() {
		return Member.builder()
			.provider(provider)
			.email(email)
			.nickname(nickname)
			.gender(gender)
			.age(age)
			.build();
	}
}
