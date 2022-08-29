package com.amondfarm.api.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.member.domain.Member;
import com.amondfarm.api.member.enums.ProviderType;

/**
 * Member Repository
 *
 * @since 2022-08-18
 * @author jwlee
 */

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByEmail(String email);

	Optional<Member> findByProviderAndEmail(ProviderType provider, String email);
}
