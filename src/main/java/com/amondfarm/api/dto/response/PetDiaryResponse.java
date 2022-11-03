package com.amondfarm.api.dto.response;

import java.sql.Timestamp;

import com.amondfarm.api.dto.PetDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetDiaryResponse {
	private String petName;
	private Timestamp birthday;
	private PetDto pets;
}
