package com.example.gguro.service.ProfileService;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;

public interface ProfileQueryService {
    ProfileResponseDTO.ProfileViewDTO getProfile(User user, Long profileId);
}
