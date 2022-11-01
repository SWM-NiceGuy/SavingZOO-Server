package com.amondfarm.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.enums.mission.MissionStatus;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
	Optional<UserMission> findBySubmissionImageUrl(String imageUrl);

	Optional<UserMission> findUserMissionById(Long userMissionId);

	@Query("select um from UserMission um where um.missionStatus = :missionStatus and um.checkUserStatus = :checkStatus")
	List<UserMission> findByMissionStatusAndCheckStatus(@Param("missionStatus") MissionStatus missionStatus, @Param("checkStatus") boolean checkStatus);
}
