package com.amondfarm.api.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;

@Getter
public class WeeklyMissionCountRequest {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
}
