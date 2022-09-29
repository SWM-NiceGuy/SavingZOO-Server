package com.amondfarm.api.domain.old;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;

import com.amondfarm.api.common.domain.BaseTimeEntity;
import com.amondfarm.api.domain.enums.Gender;
import com.amondfarm.api.domain.enums.MemberStatus;
import com.amondfarm.api.domain.enums.ProviderType;

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
	private String uid;

	@Column(nullable = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 1)
	private Gender gender;

	@Column(nullable = true)
	private int ageGroup;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int exp;

	private String characterName;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<MemberMission> memberMissions = new ArrayList<>();

	@Builder
	public Member(Long id, ProviderType provider, String uid, String email, String nickname, MemberStatus status,
		Gender gender, int ageGroup, int exp) {
		this.id = id;
		this.provider = provider;
		this.uid = uid;
		this.email = email;
		this.nickname = nickname;
		this.status = status;
		this.gender = gender;
		this.ageGroup = ageGroup;
		this.exp = exp;
	}

	public void changeExp(int exp) {
		this.exp = exp;
	}

	public void changeCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public void changeStatus(MemberStatus status) {
		this.status = status;
	}

	public void addMemberMission(MemberMission memberMission) {
		memberMissions.add(memberMission);
	}
}