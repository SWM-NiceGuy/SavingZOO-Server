package com.amondfarm.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_id")
	private Long id;

	@Column(name = "mission_name", nullable = false)
	private String name;

	@Column(name = "mission_exp", nullable = false)
	private int exp;

	@Column(name = "mission_image", nullable = false)
	private String image;

	// @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
	// private List<UserMission> userMissions = new ArrayList<>();

	@OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
	private List<MemberMission> memberMissions = new ArrayList<>();

	// public void addUserMission(UserMission userMission) {
	// 	userMissions.add(userMission);
	// }

	public void addMemberMission(MemberMission memberMission) {
		memberMissions.add(memberMission);
	}
}
