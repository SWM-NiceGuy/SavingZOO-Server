package com.amondfarm.api.member.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import com.amondfarm.api.common.domain.BaseTimeEntity;
import com.amondfarm.api.member.enums.Gender;
import com.amondfarm.api.member.enums.ProviderType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Member Model
 *
 * @since 2022-08-05
 * @author jwlee
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProviderType provider;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 1)
	private Gender gender;

	@Column(nullable = true)
	private int age;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int exp;

	@Builder
	public Member(Long id, ProviderType provider, String email, String nickname, Gender gender, int age) {
		this.id = id;
		this.provider = provider;
		this.email = email;
		this.nickname = nickname;
		this.gender = gender;
		this.age = age;
	}
}
