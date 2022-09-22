package com.amondfarm.api.util.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;

@Configuration
public class KakaoFeignConfiguration {

	@Bean
	public Client feignClient() {
		return new Client.Default(null, null);
	}
}
