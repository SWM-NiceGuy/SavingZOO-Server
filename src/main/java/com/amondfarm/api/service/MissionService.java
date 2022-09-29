package com.amondfarm.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.dto.request.admin.CreateMissionRequest;
import com.amondfarm.api.repository.MissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

	private final MissionRepository missionRepository;

	@Transactional
	public Mission createMission(CreateMissionRequest request) {
		return missionRepository.save(Mission.from(request));
	}
}
