package com.amondfarm.api.domain;

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

import com.amondfarm.api.domain.enums.character.AffectionStatus;
import com.amondfarm.api.domain.enums.character.GrowingStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCharacter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_character_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "character_id")
	private Character character;

	@Column(name = "character_nickname", nullable = false)
	private String nickname;

	@Column(name = "character_current_level", nullable = false)
	private int currentLevel;

	@Column(name = "character_current_stage", nullable = false)
	private int currentStage;

	@Column(name = "character_current_exp", nullable = false)
	private int currentExp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GrowingStatus growingStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AffectionStatus affectionStatus;


	//==연관관계 method==//
	public void setUser(User user) {
		this.user = user;
	}

	@Builder
	public UserCharacter(Character character) {
		this.character = character;
		// 초기 상태 세팅
		this.currentLevel = 1;
		this.currentStage = 1;
		this.currentExp = 0;
		this.growingStatus = GrowingStatus.GROWING;
		this.affectionStatus = AffectionStatus.YET;
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
	 * 캐릭터 레벨 변경
	 * @param level 변경할 레벨
	 */
	public void changeLevel(int level) {
		this.currentLevel = currentLevel;
	}

	/**
	 * 캐릭터 진화 단계 변경
	 * @param stage 변경할 단계
	 */
	public void changeStage(int stage) {
		this.currentStage = stage;
	}

	/**
	 * 캐릭터 진화 완료
	 */
	public void grownup() {
		this.growingStatus = GrowingStatus.GROWNUP;
	}

	/**
	 * 애정 부여 상태 변경
	 * @param status 변경할 상태값
	 */
	public void changeAffectionStatus(AffectionStatus status) {
		this.affectionStatus = status;
	}
}
