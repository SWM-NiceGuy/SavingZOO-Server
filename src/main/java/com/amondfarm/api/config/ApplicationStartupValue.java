package com.amondfarm.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class ApplicationStartupValue implements ApplicationListener<ApplicationStartedEvent> {

	@Value("${app.version}")
	private String version;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {

	}
}
