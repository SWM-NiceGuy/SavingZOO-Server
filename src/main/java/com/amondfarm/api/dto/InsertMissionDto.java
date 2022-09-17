package com.amondfarm.api.dto;

import com.amondfarm.api.domain.Mission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InsertMissionDto {
	private String title;
	private String content;
	private int exp;
	private String imageUrl;

	public Mission toEntity() {
		return Mission.builder()
			.title(this.title)
			.content(this.content)
			.exp(this.exp)
			.imageUrl(this.imageUrl)
			.build();
	}
}
