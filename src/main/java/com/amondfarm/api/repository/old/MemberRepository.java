package com.amondfarm.api.repository.old;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.old.Member;
import com.amondfarm.api.domain.enums.old.MemberStatus;
import com.amondfarm.api.domain.enums.user.ProviderType;

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
