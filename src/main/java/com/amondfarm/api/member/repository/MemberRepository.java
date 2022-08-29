package com.amondfarm.api.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.member.domain.Member;

/**
 * Member Repository
 *
 * @since 2022-08-18
 * @author jwlee
 */

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByEmail(String email);
}
