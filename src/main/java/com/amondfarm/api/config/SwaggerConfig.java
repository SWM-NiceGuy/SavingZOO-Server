package com.amondfarm.api.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
	info = @Info(title = "멋난이 개발서버 API 명세서",
		description = "멋난이팀의 멋진 친환경 서비스 API 명세서",
		version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi openApi() {
		String[] paths = {"/v1/**"};

		return GroupedOpenApi.builder()
			.group("멋난이 서비스 API v1")
			.pathsToMatch(paths)
			.build();
	}
}
