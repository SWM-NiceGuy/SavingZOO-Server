package com.amondfarm.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.PetLevelValue;

public interface PetLevelRepository extends JpaRepository<PetLevelValue, Long> {
	Optional<PetLevelValue> findByLevel(int level);
}
