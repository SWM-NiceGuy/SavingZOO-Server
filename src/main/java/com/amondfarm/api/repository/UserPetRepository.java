package com.amondfarm.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.UserPet;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {

	int countByCurrentLevelGreaterThanEqual(int level);
}
