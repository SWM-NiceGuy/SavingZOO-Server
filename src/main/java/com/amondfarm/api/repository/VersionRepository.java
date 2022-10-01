package com.amondfarm.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.Version;
import com.amondfarm.api.domain.enums.version.VersionStatus;

public interface VersionRepository extends JpaRepository<Version, Long> {
	Optional<Version> findByVersion(String version);

	Optional<Version> findByStatus(VersionStatus status);
}
