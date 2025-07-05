package com.example.gguro.converter;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import com.example.gguro.web.dto.profile.ProfileRequestDTO;
import com.example.gguro.web.dto.profile.ProfileResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileConverter {

    public static Profile addProfile(User user, ProfileRequestDTO.ProfileDTO request) {
        return Profile.builder()
                .user(user)
                .name(request.getFirstName()+request.getLastName())
                .birth(String.format("%d-%02d-%02d", request.getYear(), request.getMonth(), request.getDay()))
                .build();
    }

    public static ProfileResponseDTO.ProfileViewDTO getProfile(Profile profile) {
        return ProfileResponseDTO.ProfileViewDTO.builder()
                .profileId(profile.getId())
                .profileName(profile.getName())
                .profileBirthDate(LocalDate.parse(profile.getBirth()))
                .build();
    }

    public static ProfileResponseDTO.ProfileListViewDTO getProfileList(List<Profile> profiles) {
        return ProfileResponseDTO.ProfileListViewDTO.builder()
                .profiles(profiles.stream().map(ProfileConverter::getProfile).collect(Collectors.toList()))
                .build();
    }
}
