package com.amondfarm.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.Mission;

public interface MissionRespository extends JpaRepository<Mission, Long> {
}
