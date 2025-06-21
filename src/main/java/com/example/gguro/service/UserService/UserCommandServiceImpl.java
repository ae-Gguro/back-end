package com.example.gguro.service.UserService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.UserRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl extends UserCommandService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signUp(UserRequestDTO.UserSignUpDTO request) {
        // 비밀번호 확인
        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new UserHandler(ErrorStatus.PASSWORDS_DO_NOT_MATCH);
        }

        // 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserHandler(ErrorStatus.USER_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodPassword = passwordEncoder.encode(request.getPassword());

        User user = UserConverter.toUser(request, encodPassword);

        return userRepository.save(user);
    }
}
