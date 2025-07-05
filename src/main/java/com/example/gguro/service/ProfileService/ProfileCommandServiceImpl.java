package com.example.gguro.service.ProfileService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.converter.ProfileConverter;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService{

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    public Profile createProfile(User user, ProfileRequestDTO.ProfileDTO request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (user.getProfileList().size() == 4){
            throw new ProfileHandler(ErrorStatus.PROFILE_LIMIT_EXCEEDED);
        }

        return profileRepository.save(ProfileConverter.addProfile(user, request));
    }

    @Override
    public void deleteProfile(User user, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        user.removeProfile(profile);
        profileRepository.deleteById(profileId);
    }

    @Override
    public ProfileResponseDTO.ProfileViewDTO updateProfile(User user, Long profileId, ProfileRequestDTO.ProfileDTO request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        profile.setName(request.getFirstName()+request.getLastName());
        profile.setBirth(String.format("%d-%02d-%02d", request.getYear(), request.getMonth(), request.getDay()));
        profileRepository.save(profile);

        return ProfileConverter.getProfile(profile);
    }
}
