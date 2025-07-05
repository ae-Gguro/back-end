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
}
