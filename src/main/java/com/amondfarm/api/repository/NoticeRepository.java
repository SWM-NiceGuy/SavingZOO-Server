package com.amondfarm.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	Optional<Notice> findByApplyTrue();
}
