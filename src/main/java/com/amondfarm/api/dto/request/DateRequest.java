package com.amondfarm.api.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;

@Getter
public class DateRequest {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
}
