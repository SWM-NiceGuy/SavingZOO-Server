// package com.amondfarm.api.domain;
//
// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.EnumType;
// import javax.persistence.Enumerated;
// import javax.persistence.FetchType;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.JoinColumn;
// import javax.persistence.ManyToOne;
//
// import com.amondfarm.api.domain.enums.MissionStatus;
//
// import lombok.AccessLevel;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
//
// @Entity
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor(access = AccessLevel.PRIVATE)
// public class MemberMission {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	@Column(name = "member_mission_id")
// 	private Long id;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "member_id")
// 	private Member member;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "mission_id")
// 	private Mission mission;
//
// 	@Enumerated(EnumType.STRING)
// 	@Column(name = "mission_status", nullable = false)
// 	private MissionStatus missionStatus;
//
// 	//==생성 메소드==//
// 	public static MemberMission createMemberMission(Member member, Mission mission) {
// 		MemberMission memberMission = new MemberMission(null, member, mission, MissionStatus.YET);
// 		if (member != null) {
// 			member.addMemberMission(memberMission);
// 		}
// 		if (mission != null) {
// 			mission.addMemberMission(memberMission);
// 		}
//
// 		return userMission;
// 	}
// }
