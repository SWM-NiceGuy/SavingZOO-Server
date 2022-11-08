package com.amondfarm.api.dto.admin;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllUserInfoResponse {
	private int totalUsers;
	private List<AdminUserInfo> userInfos;
}
