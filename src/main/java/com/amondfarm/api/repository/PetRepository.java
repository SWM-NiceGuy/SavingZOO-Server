package com.amondfarm.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.Pet;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;

public interface PetRepository extends JpaRepository<Pet, Long> {

	Optional<Pet> findByAcquisitionCondition(AcquisitionCondition condition);
}
