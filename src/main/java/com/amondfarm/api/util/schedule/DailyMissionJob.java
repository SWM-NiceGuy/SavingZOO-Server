package com.amondfarm.api.util.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.amondfarm.api.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DailyMissionJob extends QuartzJobBean {

	private final UserService userService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		userService.insertDailyMissions();
	}
}
