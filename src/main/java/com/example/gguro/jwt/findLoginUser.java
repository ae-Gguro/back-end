package com.example.gguro.jwt;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.domain.User;
import com.example.gguro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class findLoginUser {
    private static UserRepository userRepository;

    @Autowired
    public findLoginUser(UserRepository userRepository) { this.userRepository = userRepository; }

    public static User getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserHandler(ErrorStatus.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(authentication.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }
}