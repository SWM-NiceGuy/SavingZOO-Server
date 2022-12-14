package com.amondfarm.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.Banner;
import com.amondfarm.api.domain.Notice;
import com.amondfarm.api.domain.Version;
import com.amondfarm.api.domain.enums.version.VersionStatus;
import com.amondfarm.api.dto.BannerDto;
import com.amondfarm.api.dto.response.BannerResponse;
import com.amondfarm.api.dto.response.CheckResponse;
import com.amondfarm.api.dto.response.NoticeResponse;
import com.amondfarm.api.repository.BannerRepository;
import com.amondfarm.api.repository.NoticeRepository;
import com.amondfarm.api.repository.VersionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UtilService {

	private final VersionRepository versionRepository;
	private final BannerRepository bannerRepository;
	private final NoticeRepository noticeRepository;

	public CheckResponse checkVersion(String clientVersion) {
		// 해당 버전의 필수 업데이트 요소 체크, 현재 최신 버전 반환
		Version currentClientVersion = versionRepository.findByVersion(clientVersion)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 버전입니다"));

		Version latestVersion = versionRepository.findByStatus(VersionStatus.LATEST)
			.orElseThrow(() -> new NoSuchElementException("최신 버전의 앱이 없습니다."));

		boolean isRequired = false;
		// 지금 클라이언트 버전이 필수 업데이트 필요 시
		if (currentClientVersion.getIsRequired() == 1) {
			isRequired = true;
		}

		return CheckResponse.builder()
			.required(isRequired)
			.latestVersion(latestVersion.getVersion())
			.releaseNote(latestVersion.getReleaseNote())
			.apiUrl(currentClientVersion.getApiUrl())
			.build();
	}

	public BannerResponse getBanners() {
		List<Banner> banners = bannerRepository.findBannersByIsApply(true);

		List<BannerDto> bannerDtos = new ArrayList<>();

		for (Banner banner : banners) {
			bannerDtos.add(
				BannerDto.builder()
					.imageUrl(banner.getImageUrl())
					.contentUrl(banner.getContentUrl())
					.build()
			);
		}

		return BannerResponse.builder()
			.totalBanners(banners.size())
			.banners(bannerDtos)
			.build();
	}

	public NoticeResponse getNotice() {
		Optional<Notice> notice = noticeRepository.findByApplyTrue();
		if (notice.isEmpty()) {
			return NoticeResponse.builder()
				.isApply(false)
				.build();
		}

		return NoticeResponse.builder()
			.isApply(true)
			.isRequired(notice.get().isRequired())
			.message(notice.get().getMessage())
			.build();
	}
}
