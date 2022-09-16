package com.amondfarm.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.MemberMission;

public interface MemberMissionRepository extends JpaRepository<MemberMission, Long> {

	// Member ID 를 이용해서 MemberMission 리스트 조회
	@Query("select mm from MemberMission mm "
		+ "join fetch mm.member m "
		+ "join fetch mm.mission ms "
		+ "where m.id = :id")
	List<MemberMission> findMemberMissionByMemberId(@Param("id") Long id);

	@Query("select mm from MemberMission mm "
			+ "join fetch mm.member m "
			+ "join fetch mm.mission ms "
			+ "where m.id = :member_id and ms.id = :mission_id")
	Optional<MemberMission> findMemberMissionByMissionId(
		@Param("member_id") Long memberId, @Param("mission_id") Long missionId);
}
