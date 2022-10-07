package com.amondfarm.api.config;

import static org.quartz.JobBuilder.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;

import com.amondfarm.api.util.schedule.DailyMissionJob;
import com.amondfarm.api.util.schedule.DailyPushJob;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

	private final Scheduler scheduler;

	@PostConstruct
	public void start() {
		JobDetail insetMissionJobDetail = buildJobDetail(DailyMissionJob.class, new HashMap());
		JobDetail pushJobDetail = buildJobDetail(DailyPushJob.class, new HashMap());

		try {
			scheduler.scheduleJob(insetMissionJobDetail, buildJobTrigger("0 0 3 * * ?"));
			scheduler.scheduleJob(pushJobDetail, buildJobTrigger("0 0 9 * * ?"));

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public Trigger buildJobTrigger(String scheduleExp) {
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
	}

	public JobDetail buildJobDetail(Class job, Map params) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(params);

		return newJob(job).usingJobData(jobDataMap).build();
	}
}
