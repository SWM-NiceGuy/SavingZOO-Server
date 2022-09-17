package com.amondfarm.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.dto.InsertMissionRequest;
import com.amondfarm.api.dto.MessageResponse;
import com.amondfarm.api.repository.MissionRespository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionService {

	private final MissionRespository missionRespository;

	@Transactional
	public MessageResponse insertMission(InsertMissionRequest request) {
		if (request.isAdmin()) {
			missionRespository.save(request.toEntity());
			return new MessageResponse("success");
		}
		return new MessageResponse("fail");
	}
}
