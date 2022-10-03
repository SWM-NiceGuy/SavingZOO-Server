package com.amondfarm.api.domain;

import java.time.LocalDateTime;

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

import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.mission.NotiTransferStatus;
import com.amondfarm.api.domain.enums.mission.VerificationStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	private LocalDateTime activatedAt;
	private LocalDateTime accomplishedAt;
	private LocalDateTime certifiedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "mission_status", nullable = false)
	private MissionStatus missionStatus;

	private String submissionImageUrl;

	private String reasonForReject;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VerificationStatus verificationStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotiTransferStatus notiTransferStatus;

	//==연관관계 method==//
	public void setUser(User user) {
		this.user = user;
	}

	@Builder
	public UserMission(Mission mission, LocalDateTime activatedAt) {
		this.mission = mission;
		// 초기 상태 세팅
		this.activatedAt = activatedAt;	// 수행 활성화 날짜
		this.missionStatus = MissionStatus.INCOMPLETE;    // 미션 수행 상태
		this.verificationStatus = VerificationStatus.YET;    // 인증 상태
		this.notiTransferStatus = NotiTransferStatus.YET;    // Noti 전송 상태
	}

	//==비즈니스 로직==//
	// 미션 수행 시
	public void doMission(String imageUrl, LocalDateTime time) {
		this.submissionImageUrl = imageUrl;
		this.missionStatus = MissionStatus.WAIT;
		this.accomplishedAt = time;
	}

	public void approveMission(LocalDateTime time) {
		this.certifiedAt = time;
		this.missionStatus = MissionStatus.COMPLETED;
		this.verificationStatus = VerificationStatus.COMPLETED;
	}

	public void rejectMission(LocalDateTime time, String reason) {
		this.certifiedAt = time;
		this.missionStatus = MissionStatus.REJECTED;
		this.verificationStatus = VerificationStatus.COMPLETED;
		this.reasonForReject = reason;
	}
}
