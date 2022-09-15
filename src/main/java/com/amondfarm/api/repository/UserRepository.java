package com.amondfarm.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.Member;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.UserStatus;
import com.amondfarm.api.domain.enums.ProviderType;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderTypeAndLoginId(ProviderType providerType, String loginId);

	@Query("select u from User u where u.providerType = :providerType and u.loginId = :loginId and u.userStatus = :userStatus")
	Optional<User> findMember(@Param("providerType") ProviderType providerType, @Param("loginId") String loginId,
		@Param("userStatus") UserStatus userStatus);
}
