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

import com.amondfarm.api.common.domain.BaseTimeEntity;
import com.amondfarm.api.domain.enums.pet.GrowingStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPet extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_pet_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@Column(name = "pet_nickname")
	private String nickname;

	@Column(name = "pet_current_level", nullable = false)
	private int currentLevel;

	@Column(name = "pet_current_stage", nullable = false)
	private int currentStage;

	@Column(name = "pet_current_exp", nullable = false)
	private int currentExp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GrowingStatus growingStatus;

	@Column(nullable = false)
	private LocalDateTime playedAt;

	@Column(nullable = false)
	private LocalDateTime birthday;

	@Column(name = "stage2_grow_date")
	private LocalDateTime stage2GrowDate;

	@Column(name = "stage3_grow_date")
	private LocalDateTime stage3GrowDate;

	@Column(name = "left_at")
	private LocalDateTime leftAt;

	//==연관관계 method==//
	public void setUser(User user) {
		this.user = user;
	}

	@Builder
	public UserPet(Pet pet) {
		this.pet = pet;
		this.playedAt = LocalDateTime.now().minusHours(4);
		// 초기 상태 세팅
		this.currentLevel = 1;
		this.currentStage = 1;
		this.currentExp = 0;
		this.growingStatus = GrowingStatus.GROWING;
		this.birthday = LocalDateTime.now();
	}

	//==비즈니스 로직==//
	/**
	 * 캐릭터 닉네임 변경
	 * @param nickname 변경할 닉네임
	 */
	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 캐릭터 경험치 변경
	 * @param exp 변경할 경험치
	 */
	public void changeExp(int exp) {
			this.currentExp = exp;
		}

	/**
	 * 캐릭터 레벨 변경
	 * @param level 변경할 레벨
	 */
	public void changeLevel(int level) {
		this.currentLevel = level;
	}

	/**
	 * 캐릭터 진화 단계 변경
	 * @param stage 변경할 단계
	 */
	public void changeStage(int stage) {
		this.currentStage = stage;
		if (stage == 2) {
			stage2GrowDate = LocalDateTime.now();
		} else if (stage == 3) {
			stage3GrowDate = LocalDateTime.now();
		}
	}

	/**
	 * 캐릭터 진화 완료
	 */
	public void grownup() {
		this.growingStatus = GrowingStatus.GROWNUP;
	}

	/**
	 * 캐릭터 놀아주기
	 */
	public void play() {
		this.playedAt = LocalDateTime.now();
	}

	/**
	 * 캐릭터 자연으로 돌려보내기
	 */
	public void leaveToNature() {
		leftAt = LocalDateTime.now();
	}
}
