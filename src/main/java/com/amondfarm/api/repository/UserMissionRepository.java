package com.amondfarm.api.repository;

<<<<<<< Updated upstream
=======
import java.time.LocalDateTime;
import java.util.Date;
>>>>>>> Stashed changes
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< Updated upstream
import org.springframework.data.repository.query.Param;
=======
>>>>>>> Stashed changes

import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.enums.mission.MissionStatus;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
	Optional<UserMission> findBySubmissionImageUrl(String imageUrl);

	Optional<UserMission> findUserMissionById(Long userMissionId);

<<<<<<< Updated upstream
	@Query("select um from UserMission um where um.id IN (:ids)")
	List<UserMission> findUserMissionsById(@Param("ids") List<Long> ids);

	@Query("select um from UserMission um where um.missionStatus = :missionStatus and um.checkUserStatus = :checkStatus")
	List<UserMission> findByMissionStatusAndCheckStatus(@Param("missionStatus") MissionStatus missionStatus, @Param("checkStatus") boolean checkStatus);
=======
	int countByAccomplishedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	List<UserMission> findByAccomplishedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
>>>>>>> Stashed changes
}
