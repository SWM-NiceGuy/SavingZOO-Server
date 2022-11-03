package com.amondfarm.api.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amondfarm.api.domain.Mission;
import com.amondfarm.api.domain.MissionExampleImage;
import com.amondfarm.api.domain.Pet;
import com.amondfarm.api.domain.PetLevelValue;
import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.UserMission;
import com.amondfarm.api.domain.UserPet;
import com.amondfarm.api.domain.enums.PushType;
import com.amondfarm.api.domain.enums.mission.MissionStatus;
import com.amondfarm.api.domain.enums.mission.MissionType;
import com.amondfarm.api.domain.enums.pet.AcquisitionCondition;
import com.amondfarm.api.domain.enums.user.UserStatus;
import com.amondfarm.api.dto.AllowPushState;
import com.amondfarm.api.dto.CreateUserDto;
import com.amondfarm.api.dto.MissionDto;
import com.amondfarm.api.dto.MissionHistory;
import com.amondfarm.api.dto.PetDto;
import com.amondfarm.api.dto.PetPlayingInfo;
import com.amondfarm.api.dto.PetStageDto;
import com.amondfarm.api.dto.SlackDoMissionDto;
import com.amondfarm.api.dto.request.ChangePetNicknameRequest;
import com.amondfarm.api.dto.request.DeviceToken;
import com.amondfarm.api.dto.request.MissionCheckRequest;
import com.amondfarm.api.dto.request.PlayWithPetRequest;
import com.amondfarm.api.dto.request.UsernameRequest;
import com.amondfarm.api.dto.response.ChangePetNicknameResponse;
import com.amondfarm.api.dto.response.CompletedMission;
import com.amondfarm.api.dto.response.DailyMissionsResponse;
import com.amondfarm.api.dto.response.MissionHistoryResponse;
import com.amondfarm.api.dto.response.MissionStateResponse;
import com.amondfarm.api.dto.response.PetDiaryResponse;
import com.amondfarm.api.dto.response.PetInfo;
import com.amondfarm.api.dto.response.PlayWithPetResponse;
import com.amondfarm.api.dto.response.RejectedMission;
import com.amondfarm.api.dto.response.RewardResponse;
import com.amondfarm.api.dto.response.UserMissionDetailResponse;
import com.amondfarm.api.dto.response.UserNameRewardResponse;
import com.amondfarm.api.repository.MissionRepository;
import com.amondfarm.api.repository.PetLevelRepository;
import com.amondfarm.api.repository.PetRepository;
import com.amondfarm.api.repository.UserMissionRepository;
import com.amondfarm.api.repository.UserPetRepository;
import com.amondfarm.api.repository.UserRepository;
import com.amondfarm.api.security.dto.UserInfoResponse;
import com.amondfarm.api.security.util.SecurityUtil;
import com.amondfarm.api.util.S3Uploader;
import com.amondfarm.api.util.SlackService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

	@Value(value = "${server-address.cdnUrl}")
	private String cdnUrl;

	private final UserRepository userRepository;
	private final UserMissionRepository userMissionRepository;
	private final UserPetRepository userPetRepository;
	private final MissionRepository missionRepository;
	private final PetRepository petRepository;
	private final PetLevelRepository petLevelRepository;

	private final S3Uploader s3Uploader;

	private final SlackService slackService;

	public User getCurrentUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(id -> userRepository.findById(Long.valueOf(id)))
			.orElseThrow(() -> new NoSuchElementException("해당 회원이 없습니다."));
	}

	// 현재 유저의 캐릭터 정보 조회
	public PetInfo getUserPetInfo() {

		// 획득조건이 DEFAULT 인 유저펫 리턴
		UserPet userPet = getCurrentUserPet(getCurrentUser());

		PetLevelValue petLevelValue = petLevelRepository.findByLevel(userPet.getCurrentLevel())
			.orElseThrow(() -> new NoSuchElementException("해당 레벨의 정보가 없습니다."));

		PetPlayingInfo petPlayingInfo = getPetPlayingInfo(userPet);

		return PetInfo.builder()
			.petId(userPet.getId())
			.image(getPetStageImage(userPet))
			.name(userPet.getPet().getPetName())
			.nickname(userPet.getNickname())
			.currentLevel(userPet.getCurrentLevel())
			.currentExp(userPet.getCurrentExp())
			.maxExp(petLevelValue.getMaxExp())
			.isPlayReady(petPlayingInfo.isPlayReady())
			.remainedPlayTime(petPlayingInfo.getRemainedPlayTime())
			.build();
	}

	private PetPlayingInfo getPetPlayingInfo(UserPet userPet) {

		LocalDateTime lastPlayedAt = userPet.getPlayedAt();
		long between = ChronoUnit.SECONDS.between(lastPlayedAt, LocalDateTime.now());

		int time = 20;

		// 현재 시간이 이전 놀아준 시간보다 4시간이 지났다면 -> 가능, 0 리턴
		if (between >= time) {
			return PetPlayingInfo.builder()
				.isPlayReady(true)
				.remainedPlayTime(0)
				.build();
		} else {
			return PetPlayingInfo.builder()
				.isPlayReady(false)
				.remainedPlayTime(time - between)
				.build();
		}
	}

	// 현재 UserPet 의 단계에 따라 이미지를 리턴하는 함수
	// TODO Fetch JOIN 으로 전체 펫 테이블을 가져와서 실루엣 이미지도 채워서 반환하기
	private String getPetStageImage(UserPet userPet) {

		Pet pet = userPet.getPet();
		switch (userPet.getCurrentStage()) {
			case 1:
				return pet.getStage1ImageUrl();
			case 2:
				return pet.getStage2ImageUrl();
			case 3:
				return pet.getStage3ImageUrl();
		}
		return null;
	}

	@Transactional
	public ChangePetNicknameResponse setUserPetNickname(ChangePetNicknameRequest changePetNicknameRequest) {

		UserPet userPet = getCurrentUser().getUserPets().stream()
			.filter(p -> p.getId() == changePetNicknameRequest.getUserPetId())
			.findFirst().orElseThrow(() -> new NoSuchElementException("해당 아이디의 캐릭터가 없습니다."));

		userPet.changeNickname(changePetNicknameRequest.getNickname());

		return ChangePetNicknameResponse.builder()
			.userPetId(userPet.getId())
			.nickname(userPet.getNickname())
			.build();
	}

	public User joinUser(UserInfoResponse userInfoResponse) {

		// 데일리 미션 찾기
		List<Mission> dailyMissions = missionRepository.findAllMissionsByMissionType(MissionType.DAILY);
		List<UserMission> userMissions = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrow = now.plusDays(1);

		for (Mission mission : dailyMissions) {
			// 오늘 날짜의 미션 삽입
			userMissions.add(new UserMission(mission, now));
			// 다음 날짜의 미션 미리 삽입
			userMissions.add(new UserMission(mission, tomorrow));
		}

		// DEFAULT 캐릭터 찾기
		Pet pet = petRepository.findByAcquisitionCondition(AcquisitionCondition.DEFAULT)
			.orElseThrow(() -> new NoSuchElementException("DEFAULT 캐릭터가 없습니다."));

		// UserPet 생성
		UserPet userPet = UserPet.builder()
			.pet(pet)
			.build();

		CreateUserDto userDto = CreateUserDto.builder()
			.loginId(userInfoResponse.getLoginId())
			.providerType(userInfoResponse.getProviderType())
			.loginUsername(userInfoResponse.getNickname())
			.email(userInfoResponse.getEmail())
			.userMissions(userMissions)
			.userPet(userPet)
			.build();

		return userRepository.save(User.from(userDto));
	}

	// 현재 날짜에 해당하는 유저만의 데일리 미션을 조회
	public DailyMissionsResponse getDailyMissions() {
		User user = getCurrentUser();
		List<MissionDto> missions = new ArrayList<>();

		List<UserMission> userMissions = user.getUserMissions().stream()
			.filter(m -> LocalDate.now().isEqual(m.getActivatedAt().toLocalDate()))
			.collect(Collectors.toList());

		for (UserMission userMission : userMissions) {
			missions.add(MissionDto.builder()
				.id(userMission.getId())
				.name(userMission.getMission().getTitle())
				.category(userMission.getMission().getMissionCategory().getName())
				.iconUrl(userMission.getMission().getIconUrl())
				.state(userMission.getMissionStatus())
				.rewardType(userMission.getMission().getRewardType())
				.reward(userMission.getMission().getReward())
				.build());
		}

		return DailyMissionsResponse.builder()
			.totalMissions(missions.size())
			.missions(missions)
			.build();
	}

	public UserMissionDetailResponse getUserMissionDetail(Long userMissionId) {
		UserMission userMission = userMissionRepository.findById(userMissionId)
			.orElseThrow(() -> new NoSuchElementException("잘못된 미션 조회입니다."));

		List<String> exampleImageUrls = new ArrayList<>();
		for (MissionExampleImage exampleImage : userMission.getMission().getExampleImages()) {
			exampleImageUrls.add(exampleImage.getImageUrl());
		}

		return UserMissionDetailResponse.builder()
			.name(userMission.getMission().getTitle())
			.description(userMission.getMission().getDescription())
			.content(userMission.getMission().getContent())
			.submitGuide(userMission.getMission().getSubmitGuide())
			.exampleImageUrls(exampleImageUrls)
			.rewardType(userMission.getMission().getRewardType())
			.reward(userMission.getMission().getReward())
			.state(userMission.getMissionStatus())
			.build();
	}

	// user 가 미션 수행 시
	// 1. S3 업로드
	// user_mission 테이블 update
	// 1. 미션 사진 url submissionImageUrl 에 저장
	// 2. 수행시각 현재 시각으로 업로드
	// 3. 미션 상태 WAIT 으로 변경
	@Transactional
	public void doMission(Long userMissionId, MultipartFile submissionImage) {

		User currentUser = getCurrentUser();

		log.info("user id : " + currentUser.getId());

		try {
			String uploadImageUrl = cdnUrl + s3Uploader.upload(submissionImage, currentUser.getId().toString());

			UserMission userMission = userMissionRepository.findUserMissionById(userMissionId)
				.orElseThrow(() -> new NoSuchElementException("잘못된 유저 미션 ID 입니다."));

			if (userMission.getUser().getId() != currentUser.getId()) {
				throw new NoSuchElementException("현재 유저의 미션이 아닙니다");
			}

			userMission.doMission(uploadImageUrl, LocalDateTime.now());

			// slack 전송
			slackService.postSlack(
				SlackDoMissionDto.builder()
					.userId(currentUser.getId())
					.loginUsername(currentUser.getLoginUsername())
					.accomplishedAt(userMission.getAccomplishedAt())
					.userMissionId(userMissionId)
					.missionName(userMission.getMission().getTitle())
					.missionImageUrl(uploadImageUrl)
					.build()
			);

		} catch (IOException e) {
			e.printStackTrace();
			log.info("S3 업로드에 실패했습니다.");
		}
	}

	@Transactional
	public void setDeviceToken(DeviceToken request) {
		getCurrentUser().changeDeviceToken(request.getDeviceToken());
	}

	@Transactional
	public AllowPushState setAllowPushState(PushType pushType, boolean pushAllowState) {
		return new AllowPushState(getCurrentUser().changeAllowPushState(pushType, pushAllowState));
	}

	public MissionHistoryResponse getMissionHistory() {
		List<UserMission> userMissions = getCurrentUser().getUserMissions();

		List<UserMission> triedMissions = userMissions.stream()
			.filter(um -> um.isMissionTried())
			.collect(Collectors.toList());

		List<MissionHistory> missionHistories = new ArrayList<>();
		for (UserMission triedMission : triedMissions) {
			missionHistories.add(MissionHistory.builder()
				.missionHistoryId(triedMission.getId())
				.date(Timestamp.valueOf(triedMission.getAccomplishedAt()).getTime())
				.title(triedMission.getMission().getTitle())
				.state(triedMission.getMissionStatus())
				.rewardType(triedMission.getMission().getRewardType())
				.reward(triedMission.getMission().getReward())
				.build()
			);
		}

		return MissionHistoryResponse.builder()
			.totalMissionHistory(missionHistories.size())
			.missionHistories(missionHistories)
			.build();
	}

	// TODO 꼭 리팩토링 하자 ..
	@Transactional
	public PlayWithPetResponse playWithPet(PlayWithPetRequest playWithPetRequest) {
		UserPet userPet = userPetRepository.findById(Long.parseLong(playWithPetRequest.getUserPetId()))
			.orElseThrow(() -> new NoSuchElementException("캐릭터가 없습니다."));

		if (userPet.getUser().getId() != getCurrentUser().getId()) {
			throw new IllegalArgumentException("접근할 수 없는 권한입니다.");
		}

		PetPlayingInfo petPlayingInfo = getPetPlayingInfo(userPet);

		if (petPlayingInfo.isPlayReady()) {
			// 놀아주기 가능
			userPet.play();

			// 경험치 5만큼 증가
			incrementExp(userPet, 5);

			PetLevelValue petLevelValue = petLevelRepository.findByLevel(userPet.getCurrentLevel())
				.orElseThrow(() -> new NoSuchElementException("해당 레벨의 정보가 없습니다."));

			PetPlayingInfo afterPlayInfo = getPetPlayingInfo(userPet);

			PetInfo petInfo = PetInfo.builder()
				.petId(userPet.getId())
				.image(getPetStageImage(userPet))
				.name(userPet.getPet().getPetName())
				.nickname(userPet.getNickname())
				.currentLevel(userPet.getCurrentLevel())
				.currentExp(userPet.getCurrentExp())
				.maxExp(petLevelValue.getMaxExp())
				.isPlayReady(afterPlayInfo.isPlayReady())
				.remainedPlayTime(afterPlayInfo.getRemainedPlayTime())
				.build();

			return PlayWithPetResponse.builder()
				.isSuccess(true)
				.petInfo(petInfo)
				.build();

		} else {
			// 놀아주기 불가능
			return PlayWithPetResponse.builder()
				.isSuccess(false)
				.msg("이전 놀아주기 이후 쿨타임이 지나지 않았습니다.")
				.build();
		}
	}

	private void incrementExp(UserPet userPet, int addExp) {

		if (userPet.getCurrentStage() < userPet.getPet().getCompletionStage()) {
			PetLevelValue petLevelValue = petLevelRepository.findByLevel(userPet.getCurrentLevel())
				.orElseThrow(() -> new NoSuchElementException("잘못된 레벨 정보입니다."));

			int afterExp = userPet.getCurrentExp() + addExp;
			if (afterExp >= petLevelValue.getMaxExp()) {    // 경험치가 현재 레벨 Max 값 이상. 레벨업 로직 수행
				userPet.changeLevel(userPet.getCurrentLevel() + 1);
				userPet.changeExp(afterExp - petLevelValue.getMaxExp());

				// 만약 레벨이 진화 조건에 해당하는 레벨이라면 해당 조건 단계로 changeStage
				int stage = userPet.getPet().checkStage(userPet.getCurrentLevel());
				if (stage != 0) {
					userPet.changeStage(stage);
					if (stage == 3) {
						// 해당 pet 에서 최고레벨 가져오기
						// 레벨 정보 테이블에서 최고레벨 맥스 경험치 가져와서 적용
						PetLevelValue maxLevelValue = petLevelRepository.findByLevel(
								userPet.getPet().getStage3Level())
							.orElseThrow(() -> new NoSuchElementException("해당 펫의 최고단계 레벨정보를 가져오는 데에 실패했습니다."));
						userPet.changeExp(maxLevelValue.getMaxExp());
					}
				}
			} else {    // 경험치가 현재 레벨 Max 값보다 작음. 레벨은 그대로, 경험치만 상승
				userPet.changeExp(afterExp);
			}
		}
	}

	// 매일 데일리 미션 추가
	@Transactional
	public void insertDailyMissions() {
		// Active 유저 조회
		List<User> allActiveUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);

		// 데일리 미션 찾기
		List<Mission> dailyMissions = missionRepository.findAllMissionsByMissionType(MissionType.DAILY);
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

		for (User activeUser : allActiveUsers) {
			for (Mission dailyMission : dailyMissions) {
				activeUser.addUserMission(new UserMission(dailyMission, tomorrow));
				log.info("[스케줄 실행] user : " + activeUser.getId() + " mission : " + dailyMission.getTitle());
			}
		}
	}

	public List<User> getActiveUser() {
		return userRepository.findAllByStatus(UserStatus.ACTIVE);
	}

	public MissionStateResponse getMissionState() {
		User currentUser = getCurrentUser();
		List<UserMission> completedUncheckMissions = userMissionRepository.findByMissionStatusAndCheckStatus(
			MissionStatus.COMPLETED, false);
		List<UserMission> rejectedUncheckMissions = userMissionRepository.findByMissionStatusAndCheckStatus(
			MissionStatus.REJECTED, false);

		List<CompletedMission> completedMissions = new ArrayList<>();
		completedUncheckMissions.stream()
			.forEach(
				userMission -> {
					completedMissions.add(
						CompletedMission.builder()
							.missionId(userMission.getId())
							.missionTitle(userMission.getMission().getTitle())
							.rewardType(userMission.getMission().getRewardType())
							.reward(userMission.getMission().getReward())
							.build()
					);
				}
			);

		List<RejectedMission> rejectedMissions = new ArrayList<>();
		rejectedUncheckMissions.stream()
			.forEach(
				userMission -> {
					rejectedMissions.add(
						RejectedMission.builder()
							.missionTitle(userMission.getMission().getTitle())
							.reason(userMission.getReasonForReject())
							.build()
					);
				}
			);

		return MissionStateResponse.builder()
			.completedMissions(completedMissions)
			.rejectedMissions(rejectedMissions)
			.totalCompletedMission(completedMissions.size())
			.totalRejectedMission(rejectedMissions.size())
			.build();
	}

	// TODO 현재 FISH reward 만 리턴
	// 확장성을 위해 다양한 reward 리턴하도록 변경 필요
	public RewardResponse getReward(MissionCheckRequest request) {

		User currentUser = getCurrentUser();

		List<UserMission> userMissions = userMissionRepository.findUserMissionsById(request.getMissionIds());
		// 요청으로 받은 아이디에 해당하는 미션들의 유저확인상태를 true 로 변경
		userMissions.forEach(UserMission::checkMission);

		// 해당 미션들의 리워드 더하기
		int sumReward = userMissions.stream()
			.mapToInt(userMission -> userMission.getMission().getReward())
			.sum();

		// 현재 유저에 리워드 더하기 및 리턴하기
		return RewardResponse.builder()
			.reward(currentUser.addReward(sumReward))
			.build();
	}

	@Transactional
	public RewardResponse feedPet() {
		User currentUser = getCurrentUser();

		if (currentUser.getRewardQuantity() <= 0) {
			throw new NoSuchElementException("줄 수 있는 먹이가 없습니다");
		}

		UserPet userPet = getCurrentUserPet(currentUser);

		incrementExp(userPet, 10);

		return RewardResponse.builder()
			.reward(currentUser.subtractReward())
			.build();
	}

	public UserNameRewardResponse getUserInfo() {
		User currentUser = getCurrentUser();
		return UserNameRewardResponse.builder()
			.username(currentUser.getLoginUsername())
			.rewardQuantity(currentUser.getRewardQuantity())
			.build();
	}

	@Transactional
	public UserNameRewardResponse setUsername(UsernameRequest request) {
		User currentUser = getCurrentUser();

		currentUser.changeUsername(request.getUsername());

		return UserNameRewardResponse.builder()
			.username(currentUser.getLoginUsername())
			.rewardQuantity(currentUser.getRewardQuantity())
			.build();
	}

	// public PetDiaryResponse getPetDiary() {
	// 	// 현재 유저의 dafault 캐릭터를 가져오기
	// 	UserPet currentUserPet = getCurrentUserPet(getCurrentUser());
	//
	// 	// 해당 캐릭터 정보를 바탕으로 DTO 채우기
	// 	return PetDiaryResponse.builder()
	// 		.petName(currentUserPet.getNickname())
	// 		.birthday(Timestamp.valueOf(currentUserPet.getBirthday()))
	// 		// .pets(getPetDto(currentUserPet))
	// 		.build();
	// }
	//
	// private UserPet getCurrentUserPet(User CurrentUser) {
	// 	return CurrentUser.getUserPets().stream()
	// 		.filter(up -> up.getPet().getAcquisitionCondition() == AcquisitionCondition.DEFAULT)
	// 		.findFirst().orElseThrow(() -> new NoSuchElementException("캐릭터가 없습니다."));
	// }
	//
	// private void getPetDto(UserPet currentUserPet) {
	// // private PetDto getPetDto(UserPet currentUserPet) {
	//
	// 	int currentStage = currentUserPet.getCurrentStage();
	//
	// 	PetStageDto stage1 = PetStageDto.builder()
	// 		.growState(true)
	// 		// TODO description 작성하기
	// 		.description("1단계 성장일기 설명")
	// 		.level(1)
	// 		.weight(currentUserPet.getPet().getStage1Weight())
	// 		.height(currentUserPet.getPet().getStage1Height())
	// 		.grownDate(Timestamp.valueOf(currentUserPet.getBirthday()))
	// 		.build();
	//
	// 	PetStageDto stage2 = PetStageDto.builder()
	// 		// 현재 캐릭터 stage
	// 		// .growState()
	// 		// TODO description 작성하기
	// 		.description("1단계 성장일기 설명")
	// 		.level(currentUserPet.getPet().getStage2Level())
	// 		.weight(currentUserPet.getPet().getStage1Weight())
	// 		.height(currentUserPet.getPet().getStage1Height())
	// 		.silhouetteImageUrl(currentUserPet.getPet().getStage2SilhouetteUrl())
	// 		.grownDate(Timestamp.valueOf(currentUserPet.getBirthday()))
	// 		.build();
	//
	// 	PetStageDto stage3 = PetStageDto.builder().build();
	// }
}