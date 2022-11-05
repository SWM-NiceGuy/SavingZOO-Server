package com.amondfarm.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "banner_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String imageUrl;

	private String contentUrl;

	@Column(nullable = false)
	private boolean isApply;

	@Builder
	public Banner(String title, String imageUrl, String contentUrl, boolean isApply) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.contentUrl = contentUrl;
		this.isApply = isApply;
	}
}
