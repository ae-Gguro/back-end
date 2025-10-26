package com.example.gguro.service.ProfileService;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileCommandService {
    Profile createProfile(User user, ProfileRequestDTO.ProfileDTO request, MultipartFile image);
    void deleteProfile(User user, Long profileId);
    ProfileResponseDTO.ProfileViewDTO updateProfile(User user, Long profileId, ProfileRequestDTO.ProfileDTO request, MultipartFile image);
}
