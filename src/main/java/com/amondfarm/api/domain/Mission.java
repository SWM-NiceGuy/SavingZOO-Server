package com.amondfarm.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amondfarm.api.domain.enums.mission.MissionType;
import com.amondfarm.api.domain.enums.mission.RewardType;
import com.amondfarm.api.dto.request.admin.CreateMissionRequest;

import lombok.AccessLevel;
import lombok.Builder;
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

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String reasonForMission;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MissionType missionType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RewardType rewardType;

	@Column(nullable = false)
	private int reward;

	@Column(name = "profile_image_url", nullable = false)
	private String imageUrl;

	@Builder
	private Mission(String title, String description, String reasonForMission, MissionType missionType,
		RewardType rewardType, int reward, String imageUrl) {
		this.title = title;
		this.description = description;
		this.reasonForMission = reasonForMission;
		this.missionType = missionType;
		this.rewardType = rewardType;
		this.reward = reward;
		this.imageUrl = imageUrl;
	}

	//==생성 메소드==//
	public static Mission from(CreateMissionRequest request) {
		return Mission.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.reasonForMission(request.getReasonForMission())
			.missionType(request.getMissionType())
			.rewardType(request.getRewardType())
			.reward(request.getReward())
			.imageUrl(request.getProfileImageUrl())
			.build();
	}
}
