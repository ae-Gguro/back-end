package com.example.gguro.service.UserService;

import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.UserHandler;
import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.BlacklistedToken;
import com.example.gguro.domain.User;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.BlacklistedTokenRepository;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.service.DeviceService.DeviceCommandService;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserRequestDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final DeviceCommandService deviceCommandService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

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
                user.getId(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    @Override
    public UserResponseDTO.UserLoginResponseDTO reissueToken(String refreshToken) {
        try {
            // RefreshToken 검증
            tokenProvider.validateRefreshToken(refreshToken);

            // 새 AccessToken + RefreshToken 발급
            TokenDTO tokenDTO = tokenProvider.reissueToken(refreshToken);

            return UserConverter.toUserLoginResponseDTO(tokenDTO);

        } catch (ExpiredJwtException eje) {
            throw new UserHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (IllegalArgumentException iae) {
            throw new UserHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    public void logout(HttpServletRequest request, String deviceToken) {
        try {
            String accessToken = tokenProvider.resolveAccessToken(request);

            if (accessToken == null) {
                throw new UserHandler(ErrorStatus.INVALID_TOKEN);
            }

            if (tokenProvider.validateToken(accessToken)) {
                String userId = tokenProvider.getUserIdFromToken(accessToken);

                System.out.println("이제 디바이스 토큰 비활성화");
                // 디바이스 토큰 비활성화
                deviceCommandService.deactivateDeviceToken(Long.valueOf(userId), deviceToken);

                System.out.println("이제 블랙리스트에 저장");
                // 블랙리스트에 저장
                LocalDateTime expiration = LocalDateTime.now().plusSeconds(
                        tokenProvider.getRemainingExpiration(accessToken) / 1000
                );

                BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                        .token(accessToken)
                        .expiration(expiration)
                        .build();

                blacklistedTokenRepository.save(blacklistedToken);

            } else {
                throw new UserHandler(ErrorStatus.TOKEN_EXPIRED);
            }

        } catch (ExpiredJwtException e) {
            throw new UserHandler(ErrorStatus.TOKEN_EXPIRED);
        }
    }

    @Override
    public void deleteUser(User user) {
        // 프로필까지 삭제됨
        userRepository.delete(user);
        // todo:
    }
}
