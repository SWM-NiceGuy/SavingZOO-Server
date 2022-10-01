// package com.amondfarm.api.dto;
//
// import com.amondfarm.api.domain.old.MemberMission;
// import com.amondfarm.api.domain.enums.old.MissionStatus2;
//
// import lombok.Builder;
// import lombok.Getter;
//
// @Getter
// public class MissionInfoDto {
// 	private Long id;
// 	private String imageUrl;
// 	private String title;
// 	private String content;
// 	private int reward;
// 	private MissionStatus2 state;
//
// 	@Builder
// 	public MissionInfoDto(Long id, String imageUrl, String title, String content, int reward, MissionStatus2 state) {
// 		this.id = id;
// 		this.imageUrl = imageUrl;
// 		this.title = title;
// 		this.content = content;
// 		this.reward = reward;
// 		this.state = state;
// 	}
//
// 	public static MissionInfoDto from(MemberMission memberMission) {
// 		return MissionInfoDto.builder()
// 			.id(memberMission.getMission().getId())
// 			.imageUrl(memberMission.getMission().getImageUrl())
// 			.title(memberMission.getMission().getTitle())
// 			.content(memberMission.getMission().getDescription())
// 			.reward(memberMission.getMission().getReward())
// 			.state(memberMission.getMissionStatus2())
// 			.build();
// 	}
// }
