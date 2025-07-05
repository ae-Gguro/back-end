package com.example.gguro.converter;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.ProfileRequestDTO;

public class ProfileConverter {

    public static Profile addProfile(User user, ProfileRequestDTO.ProfileDTO request) {
        return Profile.builder()
                .user(user)
                .name(request.getFirstName()+request.getLastName())
                .birth(String.format("%d-%02d-%02d", request.getYear(), request.getMonth(), request.getDay()))
                .build();
    }
}
