package com.example.gguro.service.ProfileService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.converter.ProfileConverter;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
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
}
