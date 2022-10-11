package com.amondfarm.api.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Version;
import com.amondfarm.api.domain.enums.version.VersionStatus;
import com.amondfarm.api.dto.response.CheckResponse;
import com.amondfarm.api.repository.VersionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UtilService {

	private final VersionRepository versionRepository;

	public CheckResponse checkVersion(String clientVersion) {
		// 해당 버전의 필수 업데이트 요소 체크, 현재 최신 버전 반환
		Version currentClientVersion = versionRepository.findByVersion(clientVersion)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 버전입니다"));

		Version latestVersion = versionRepository.findByStatus(VersionStatus.LATEST)
			.orElseThrow(() -> new NoSuchElementException("최신 버전의 앱이 없습니다."));

		boolean isRequired = false;
		// 지금 클라이언트 버전이 필수 업데이트가 필요 or 새로 나온 버전으로 무조건 업데이트 해야 하는 경우
		if (currentClientVersion.getIsRequired() == 1 || latestVersion.getIsRequired() == 1) {
			isRequired = true;
		}

		return CheckResponse.builder()
			.required(isRequired)
			.latestVersion(latestVersion.getVersion())
			.releaseNote(latestVersion.getReleaseNote())
			.build();
	}
}
