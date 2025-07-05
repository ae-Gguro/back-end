package com.example.gguro.service.ProfileService;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;

public interface ProfileCommandService {
    Profile createProfile(User user, ProfileRequestDTO.ProfileDTO request);
    void deleteProfile(User user, Long profileId);
}
