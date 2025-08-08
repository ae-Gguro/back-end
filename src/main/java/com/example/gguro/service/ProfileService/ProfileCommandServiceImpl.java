package com.example.gguro.service.ProfileService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.ProfileHandler;
import com.example.gguro.aws.s3.AmazonS3Manager;
import com.example.gguro.converter.ProfileConverter;
import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.domain.Uuid;
import com.example.gguro.repository.ProfileRepository;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService{

    private final ProfileRepository profileRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Override
    public Profile createProfile(User user, ProfileRequestDTO.ProfileDTO request, MultipartFile image) {

        if (user.getProfileList().size() == 4){
            throw new ProfileHandler(ErrorStatus.PROFILE_LIMIT_EXCEEDED);
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            Uuid uuid = Uuid.create();
            String keyName = amazonS3Manager.generateProfileImageKeyName(uuid);
            imageUrl = amazonS3Manager.uploadFile(keyName, image);
        }

        Profile profile = ProfileConverter.addProfile(user, request);
        profile.setImageUrl(imageUrl);

        return profileRepository.save(profile);
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
    public ProfileResponseDTO.ProfileViewDTO updateProfile(User user, Long profileId, ProfileRequestDTO.ProfileDTO request, MultipartFile image) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND));

        // 해당 프로필이 이 유저의 프로필이 맞는지
        if(!profile.getUser().equals(user)) {
            throw new ProfileHandler(ErrorStatus.PROFILE_NOT_FOUND);
        }

        profile.setName(request.getLastName()+request.getFirstName());
        profile.setBirth(String.format("%d-%02d-%02d", request.getYear(), request.getMonth(), request.getDay()));

        if (image != null && !image.isEmpty()) {
            Uuid uuid = Uuid.create();
            String keyName = amazonS3Manager.generateProfileImageKeyName(uuid);
            String imageUrl = amazonS3Manager.uploadFile(keyName, image);
            profile.setImageUrl(imageUrl);
        }

        profileRepository.save(profile);

        return ProfileConverter.getProfile(profile);
    }
}
