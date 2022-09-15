package com.amondfarm.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import com.amondfarm.api.domain.enums.MissionStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_mission_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@Enumerated(EnumType.STRING)
	@Column(name = "mission_status", nullable = false)
	private MissionStatus missionStatus;

	// //==생성 메소드==//
	// public static UserMission createUserMission(User user, Mission mission) {
	// 	UserMission userMission = new UserMission(null, user, mission, MissionStatus.YET);
	// 	if (user != null) {
	// 		user.addUserMission(userMission);
	// 	}
	// 	if (mission != null) {
	// 		mission.addUserMission(userMission);
	// 	}
	//
	// 	return userMission;
	// }
}
