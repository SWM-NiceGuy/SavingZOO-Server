package com.amondfarm.api.util.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoAccount {
	private Profile profile;
	private String gender;
	private String birthday;
	private String email;

	@Getter
	@ToString
	public class Profile {
		private String nickname;
	}
}