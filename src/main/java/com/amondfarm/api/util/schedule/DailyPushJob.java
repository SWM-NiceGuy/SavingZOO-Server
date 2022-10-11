package com.amondfarm.api.util.schedule;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.service.UserService;
import com.amondfarm.api.util.FCMService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DailyPushJob extends QuartzJobBean {

	private final UserService userService;
	private final FCMService fcmService;


	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		List<User> users = userService.getActiveUser();
		users.stream()
			.filter(u -> u.getDeviceToken() != null)
			.forEach(user -> fcmService.sendMessageTo(user.getDeviceToken(),
				"데일리미션 초기화", "미션이 새롭게 갱신되었어요. 오늘도 겸사겸사 지구를 살려보아요!"));
	}
}
