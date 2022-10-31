package com.amondfarm.api.domain;

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

import com.amondfarm.api.common.domain.BaseTimeEntity;
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
public class Mission extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mission_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String iconUrl;

	@Column(nullable = false)
	private String submitGuide;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MissionType missionType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RewardType rewardType;

	@Column(nullable = false)
	private int reward;

	@OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
	private List<MissionExampleImage> exampleImages = new ArrayList<>();

	//==연관관계 method==//
	public void addExampleImage(MissionExampleImage exampleImage) {
		exampleImages.add(exampleImage);
		exampleImage.setMission(this);
	}

	@Builder
	private Mission(String title, String description, String content, String iconUrl, String submitGuide, MissionType missionType,
		RewardType rewardType, int reward) {
		this.title = title;
		this.description = description;
		this.content = content;
		this.iconUrl = iconUrl;
		this.submitGuide = submitGuide;
		this.missionType = missionType;
		this.rewardType = rewardType;
		this.reward = reward;
	}

	//==생성 메소드==//
	public static Mission from(CreateMissionRequest request) {
		Mission mission = Mission.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.submitGuide(request.getSubmitGuide())
			.missionType(request.getMissionType())
			.rewardType(request.getRewardType())
			.reward(request.getReward())
			.build();

		return mission;
	}
}
