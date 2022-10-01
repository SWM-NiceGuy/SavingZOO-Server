package com.amondfarm.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMissionDetailResponse {
	private String name;
	private String description;
	private String content;
	private String submitGuide;
	private List<String> exampleImageUrls;

	@Builder
	public UserMissionDetailResponse(String name, String description, String content, String submitGuide,
		List<String> exampleImageUrls) {
		this.name = name;
		this.description = description;
		this.content = content;
		this.submitGuide = submitGuide;
		this.exampleImageUrls = exampleImageUrls;
	}
}
