package com.amondfarm.api.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amondfarm.api.common.domain.BaseTimeEntity;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pet_id")
	private Long id;

	@Column(nullable = false)
	private String petName;

	@Column(nullable = false)
	private String description;

	@Column(name = "stage1_weight", nullable = false)
	private BigDecimal stage1Weight;

	@Column(name = "stage1_height", nullable = false)
	private int stage1Height;

	@Column(name = "stage1_image_url", nullable = false)
	private String stage1ImageUrl;

	@Column(name = "stage2_weight", nullable = false)
	private BigDecimal stage2Weight;

	@Column(name = "stage2_height", nullable = false)
	private int stage2Height;

	@Column(name = "stage2_silhouette_image_url", nullable = false)
	private String stage2SilhouetteUrl;

	@Column(name = "stage2_image_url", nullable = false)
	private String stage2ImageUrl;

	@Column(name = "stage2_level", nullable = false)
	private int stage2Level;

	@Column(name = "stage3_weight", nullable = false)
	private BigDecimal stage3Weight;

	@Column(name = "stage3_height", nullable = false)
	private int stage3Height;

	@Column(name = "stage3_silhouette_image_url", nullable = false)
	private String stage3SilhouetteUrl;

	@Column(name = "stage3_image_url", nullable = false)
	private String stage3ImageUrl;

	@Column(name = "stage3_level", nullable = false)
	private int stage3Level;

	@Column(nullable = false)
	private int completionStage;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AcquisitionCondition acquisitionCondition;

	//==비즈니스 로직==//
	// 진화 조건에 맞는 레벨에 달성하였는지 체크 후 해당 단계 리턴. 없으면 0 리턴
	public int checkStage(int currentLevel) {
		if (currentLevel == stage2Level) {
			return 2;
		} else if (currentLevel == stage3Level) {
			return 3;
		}
		return 0;
	}
}
