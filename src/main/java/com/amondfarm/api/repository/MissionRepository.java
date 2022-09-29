package com.amondfarm.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.domain.enums.CategoryType;
import com.amondfarm.api.domain.enums.mission.MissionType;

public interface MissionRepository extends JpaRepository<Mission, Long> {
	@Query("select m from Mission m where m.missionType = :type")
	List<Mission> findAllMissionsByMissionType(@Param("type") MissionType type);
}
