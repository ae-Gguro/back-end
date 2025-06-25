package com.example.gguro.service.UserService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO.UserInfoDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.toUserInfoDTO(user);
    }
}
