package com.amondfarm.api.dto;

import com.amondfarm.api.domain.MemberMission;
import com.amondfarm.api.domain.enums.MissionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionInfoDto {
	private Long id;
	private String imageUrl;
	private String title;
	private String content;
	private int reward;
	private MissionStatus state;

	@Builder
	public MissionInfoDto(Long id, String imageUrl, String title, String content, int reward, MissionStatus state) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.title = title;
		this.content = content;
		this.reward = reward;
		this.state = state;
	}

	public static MissionInfoDto from(MemberMission memberMission) {
		return MissionInfoDto.builder()
			.id(memberMission.getMission().getId())
			.imageUrl(memberMission.getMission().getImageUrl())
			.title(memberMission.getMission().getTitle())
			.content(memberMission.getMission().getContent())
			.reward(memberMission.getMission().getExp())
			.state(memberMission.getMissionStatus())
			.build();
	}
}
