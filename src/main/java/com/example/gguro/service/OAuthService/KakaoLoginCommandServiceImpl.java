package com.example.gguro.service.OAuthService;

import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.kakao.KakaoLoginResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoLoginCommandServiceImpl implements KakaoLoginCommandService {
    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO.UserLoginResponseDTO login(String accessToken) {
        KakaoLoginResponseDTO.KakaoUserInfo kakaoUserInfo = getUserInfo(accessToken);
        String email = kakaoUserInfo.getKakaoAccount().getEmail();
        String nickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();

        // 이메일로 유저가 있는지 확인
        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user;

        if (optionalUser.isPresent()) {
            // 사용자가 있을 경우
            user = optionalUser.get();
        } else {
            // 사용자가 없을 경우
            user = UserConverter.toUserWithEmail(email, SocialType.KAKAO, nickname);
            userRepository.save(user);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    // 비동기 처리
    private KakaoLoginResponseDTO.KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoLoginResponseDTO.KakaoUserInfo.class)
                .block();
    }
}
