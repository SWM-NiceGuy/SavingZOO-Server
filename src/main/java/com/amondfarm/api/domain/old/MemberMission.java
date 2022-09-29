package com.amondfarm.api.domain.old;

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

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.domain.enums.old.MissionStatus2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_mission_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@Enumerated(EnumType.STRING)
	@Column(name = "mission_status", nullable = false)
	private MissionStatus2 missionStatus2;

	public MemberMission(Member member, Mission mission) {
		this.member = member;
		this.mission = mission;
		this.missionStatus2 = MissionStatus2.INCOMPLETE;
	}

	public void completeMission() {
		this.missionStatus2 = MissionStatus2.COMPLETE;
	}
}
