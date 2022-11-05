package com.amondfarm.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amondfarm.api.domain.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {
	List<Banner> findBannersByIsApply(boolean isApply);
}
