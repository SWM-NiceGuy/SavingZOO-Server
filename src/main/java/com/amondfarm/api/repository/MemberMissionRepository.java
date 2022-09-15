package com.amondfarm.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.Member;
import com.amondfarm.api.domain.MemberMission;
import com.amondfarm.api.domain.Mission;

public interface MemberMissionRepository extends JpaRepository<MemberMission, Long> {
	@Query("select mm from MemberMission mm "
		+ "join fetch mm.member m "
		+ "join fetch mm.mission ms "
		+ "where m.id = :id")
	List<MemberMission> findAllWithMemberAndMission(@Param("id") Long id);
}
