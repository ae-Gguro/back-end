package com.example.gguro.service.UserService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

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

    @Override
    public UserResponseDTO.UserLoginResponseDTO login(UserRequestDTO.UserLogInDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserHandler(ErrorStatus.INVALID_PASSWORD);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        return UserResponseDTO.UserLoginResponseDTO.builder()
                .accessToken(tokenDTO.getAccessToken())
                .grantType(tokenDTO.getGrantType())
                .expiresIn(tokenDTO.getAccessTokenExpiresIn())
                .build();
    }
}
