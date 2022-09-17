package com.amondfarm.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

	@Column(name = "mission_title")
	private String title;

	@Column(name = "mission_content", nullable = false)
	private String content;

	@Column(name = "mission_exp", nullable = false)
	private int exp;

	@Column(name = "mission_image_url", nullable = false)
	private String imageUrl;

	@Builder
	public Mission(String title, String content, int exp, String imageUrl) {
		this.title = title;
		this.content = content;
		this.exp = exp;
		this.imageUrl = imageUrl;
	}
}
