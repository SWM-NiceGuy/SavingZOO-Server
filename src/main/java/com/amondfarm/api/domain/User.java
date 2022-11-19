package com.amondfarm.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.amondfarm.api.common.domain.BaseTimeEntity;
import com.amondfarm.api.domain.enums.PushType;
import com.amondfarm.api.domain.enums.user.Gender;
import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.domain.enums.user.RoleType;
import com.amondfarm.api.domain.enums.user.UserStatus;
import com.amondfarm.api.dto.CreateUserDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProviderType providerType;

	// 소셜로그인 id
	@Column(nullable = false)
	private String loginId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	@Column(name = "reason_for_withdraw")
	private String reasonForWithdraw;

	private String accountUsername;

	private String loginUsername;

	private Gender gender;

	private String email;

	@Enumerated(EnumType.STRING)
	private RoleType roleType;

	private String deviceToken;

	@Column(name = "is_allow_push", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean isAllowPush;

	@Column(name = "is_allow_etc_push", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean isAllowEtcPush;

	@Column(name = "app_version")
	private String appVersion;

	@Column(name = "reward_quantity", nullable = false)
	private int rewardQuantity;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<UserMission> userMissions = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<UserPet> userPets = new ArrayList<>();

	//==연관관계 method==//
	public void addUserMission(UserMission userMission) {
		userMissions.add(userMission);
		userMission.setUser(this);
	}

	// TODO 연관관계 Set
	public void addUserPet(UserPet userPet) {
		userPets.add(userPet);
		userPet.setUser(this);
	}

	/**
	 * 생성 method
	 * @param userDto 회원가입 시 유저 정보와 2일치 미션 정보 Set
	 * @return
	 */
	// TODO User 생성 시 UserPet add 하는 로직 추가
	public static User from(CreateUserDto userDto) {
		User user = User.builder()
			.providerType(userDto.getProviderType())
			.loginId(userDto.getLoginId())
			.accountUsername(userDto.getAccountUsername())
			.loginUsername(userDto.getLoginUsername())
			.build();

		for (UserMission usermission : userDto.getUserMissions()) {
			user.addUserMission(usermission);
		}

		user.addUserPet(userDto.getUserPet());

		return user;
	}

	@Builder
	public User(ProviderType providerType, String loginId, String accountUsername, String loginUsername,
		Gender gender, String email) {

		this.providerType = providerType;
		this.loginId = loginId;
		this.accountUsername = accountUsername;
		this.loginUsername = loginUsername;
		this.gender = gender;
		this.email = email;
		this.status = UserStatus.ACTIVE;
		this.roleType = RoleType.USER;
		this.isAllowPush = false;
		this.isAllowEtcPush = false;
		this.rewardQuantity = 0;
	}

	//==비즈니스 로직==//
	public void changeStatus(UserStatus status) {
		this.status = status;
	}

	public boolean isActivate() {
		return this.status.equals(UserStatus.ACTIVE);
	}

	public void changeDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public boolean changeAllowPushState(PushType pushType, boolean state) {
		if (pushType == PushType.MISSION) {
			this.isAllowPush = state;
		} else if (pushType == PushType.ETC) {
			this.isAllowEtcPush = state;
		}
		return state;
	}

	public int addReward(int rewardQuantity) {
		this.rewardQuantity += rewardQuantity;
		return this.rewardQuantity;
	}

	public int subtractReward() {
		this.rewardQuantity -= 1;
		return this.rewardQuantity;
	}

	public String changeUsername(String username) {
		this.loginUsername = username;
		return this.loginUsername;
	}
}
