package com.amondfarm.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.domain.enums.user.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderTypeAndLoginId(ProviderType providerType, String loginId);

	@Query("select u from User u where u.providerType = :providerType and u.loginId = :loginId and u.status = :userStatus")
	Optional<User> findMember(@Param("providerType") ProviderType providerType, @Param("loginId") String loginId,
		@Param("userStatus") UserStatus userStatus);

	// userId 와 userPetId 에 해당하는 userPetId 를 반환
	@Query("select u from User u join fetch UserPet up where u.id = :userId")
	Optional<User> findUserPet(@Param("userId") Long userId);

	// TODO fetch join 빼자. 그냥 LAZY 로딩 하면 됨. 어차피 한 유저의 UserMission 이라서
	@Query("select u from User u join fetch UserMission um where u.id = :userId")
	Optional<User> findAllUserMissions(@Param("userId") Long userId);

	List<User> findAllByStatus(UserStatus active);
}