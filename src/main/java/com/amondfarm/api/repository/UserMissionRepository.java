package com.amondfarm.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.UserMission;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
	Optional<UserMission> findBySubmissionImageUrl(String imageUrl);
}
