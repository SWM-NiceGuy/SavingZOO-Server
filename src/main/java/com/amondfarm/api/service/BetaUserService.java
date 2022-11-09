package com.amondfarm.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amondfarm.api.domain.PetLevelValue;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.mission.RewardType;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
import com.amondfarm.api.dto.response.BetaUserRewardResponse;
import com.amondfarm.api.repository.PetLevelRepository;
import com.amondfarm.api.repository.UserPetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BetaUserService {

	private final UserService userService;
	private final UserPetRepository userPetRepository;
	private final PetLevelRepository petLevelRepository;


	@Transactional
	public BetaUserRewardResponse getBetaUserReward() {

		User currentUser = userService.getCurrentUser();
		// 현재 유저에게 BETA 캐릭터가 있는지 판단.
		Optional<UserPet> userPet = currentUser.getUserPets().stream()
			.filter(up -> up.getPet().getAcquisitionCondition() == AcquisitionCondition.BETA)
			.findFirst();

		// BETA 캐릭터가 없다면 false 반환
		if (userPet.isEmpty()) {
			return BetaUserRewardResponse.builder()
				.isBetaUser(false)
				.build();
		}

		// 리워드 계산하기
		UserPet userBetaPet = userPet.get();
		int reward = 0;
		for (int i = 1; i < userBetaPet.getCurrentLevel(); i++) {
			PetLevelValue levelValue = petLevelRepository.findByLevel(i).get();
			reward += levelValue.getMaxExp();
		}
		reward += userBetaPet.getCurrentExp();
		reward  = reward / 10 + 1;

		// 유저 펫 정보 삭제하고 리턴하기
		userPetRepository.delete(userBetaPet);

		return BetaUserRewardResponse.builder()
			.isBetaUser(true)
			.rewardType(RewardType.FISH)
			.reward(reward)
			.build();
	}
}
