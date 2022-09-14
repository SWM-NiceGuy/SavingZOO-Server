package com.amondfarm.api.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.enums.MemberStatus;
import com.amondfarm.api.member.enums.ProviderType;

/**
 * Member Repository
 *
 * @since 2022-08-18
 * @author jwlee
 */

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByProviderAndUid(ProviderType provider, String uid);

	@Query("select m from Member m where m.provider = :provider and m.uid = :uid and m.status = :status")
	Optional<Member> findMember(@Param("provider") ProviderType provider, @Param("uid") String uid, @Param("status") MemberStatus status);
}
