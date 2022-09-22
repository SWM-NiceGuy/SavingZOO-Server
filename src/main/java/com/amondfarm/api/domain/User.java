package com.amondfarm.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.domain.enums.RoleType;
import com.amondfarm.api.domain.enums.UserStatus;
import com.amondfarm.api.security.dto.LoginUserInfoDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 소셜로그인 uid
	@Column(nullable = false)
	private String loginId;

	// 소셜로그인 Provider
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProviderType providerType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus;

	private String email;

	private String nickname;

	private String profileImage;

	private String birthday;

	@Enumerated(EnumType.STRING)
	private RoleType roleType;

	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	// private List<UserMission> userMissions = new ArrayList<>();

	@Builder
	public User(Long id, String loginId, ProviderType providerType, UserStatus userStatus, String email,
		String nickname,
		String profileImage, String birthday, RoleType roleType) {
		this.id = id;
		this.loginId = loginId;
		this.providerType = providerType;
		this.userStatus = userStatus;
		this.email = email;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.birthday = birthday;
		this.roleType = roleType;
	}

	public boolean isActivate() {
		return userStatus.equals(UserStatus.ACTIVE);
	}

	// public void addUserMission(UserMission userMission) {
	// 	userMissions.add(userMission);
	// }

	public void changeStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public static User from(LoginUserInfoDto loginUserInfoDto) {
		return User.builder()
			.loginId(loginUserInfoDto.getLoginId())
			.providerType(loginUserInfoDto.getProviderType())
			.nickname(loginUserInfoDto.getNickname())
			.profileImage(loginUserInfoDto.getProfileImage())
			.email(loginUserInfoDto.getEmail())
			.roleType(RoleType.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
	}
}
