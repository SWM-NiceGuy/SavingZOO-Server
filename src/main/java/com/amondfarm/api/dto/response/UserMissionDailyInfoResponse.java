package com.amondfarm.api.dto.response;

import java.util.List;

import com.amondfarm.api.dto.UserDailyMissionInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMissionDailyInfoResponse {
	private int totalUsers;
	private List<UserDailyMissionInfo> userMissionInfos;
}
