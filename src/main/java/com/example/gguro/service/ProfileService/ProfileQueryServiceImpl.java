package com.example.gguro.service.ProfileService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.converter.ProfileConverter;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileQueryServiceImpl  implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    @Override
    public ProfileResponseDTO.ProfileViewDTO getProfile(User user, Long profileId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        return ProfileConverter.getProfile(profile);
    }

    @Override
    public ProfileResponseDTO.ProfileListViewDTO getProfileList(User user) {

        List<Profile> profiles = profileRepository.findAllByUserId(user.getId());

        return ProfileConverter.getProfileList(profiles);
    }

    @Override
    public String getProfileFirstNamePossessiveMarker(User user, Long profileId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        return getVocativeNameNominativeCaseMarker(profile.getFirstName());
    }

    @Override
    public String getProfileFirstNameNominativeCaseMarker(User user, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        return getVocativeNamePossessiveMarker(profile.getFirstName());
    }

    // 소유격 조사
    private String getVocativeNamePossessiveMarker(String name) {
        if (name == null || name.isEmpty()) return "";

        char lastChar = name.charAt(name.length() - 1);
        // 한글 범위 (가 ~ 힣)
        if (lastChar < 0xAC00 || lastChar > 0xD7A3) {
            return name; // 한글 아님 → 그대로 반환
        }

        int code = lastChar - 0xAC00;
        int jong = code % 28; // 종성(받침)

        if (jong == 0) {
            return name + "야"; // 받침 없음
        } else {
            return name + "아"; // 받침 있음
        }
    }

    // 주격 조사
    private String getVocativeNameNominativeCaseMarker(String name) {
        if (name == null || name.isEmpty()) return "";

        char lastChar = name.charAt(name.length() - 1);
        // 한글 범위 (가 ~ 힣)
        if (lastChar < 0xAC00 || lastChar > 0xD7A3) {
            return name; // 한글 아님 → 그대로 반환
        }

        int code = lastChar - 0xAC00;
        int jong = code % 28; // 종성(받침)

        if (jong == 0) {
            return name + "가"; // 받침 없음
        } else {
            return name + "이가"; // 받침 있음
        }
    }
}
