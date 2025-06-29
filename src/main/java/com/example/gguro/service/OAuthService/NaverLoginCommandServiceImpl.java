package com.example.gguro.service.OAuthService;

import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.naver.NaverLoginResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NaverLoginCommandServiceImpl implements NaverLoginCommandService { // 인터페이스 이름 변경
    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO.UserLoginResponseDTO login(String accessToken) {
        NaverLoginResponseDTO.NaverUserInfo naverUserInfo = getUserInfo(accessToken);

        String email = naverUserInfo.getResponse().getEmail();
        String nickname = naverUserInfo.getResponse().getName();

        // 이메일이 있는지 확인
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            // 사용자가 있을 경우
            user = optionalUser.get();
        } else {
            // 사용자가 없을 경우
            user = UserConverter.toUserWithEmail(email, SocialType.NAVER, nickname);
            userRepository.save(user);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    private NaverLoginResponseDTO.NaverUserInfo getUserInfo(String accessToken) {
        try {
            return webClient.get()
                    .uri("https://openapi.naver.com/v1/nid/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> Mono.error(new RuntimeException("네이버 API 호출 실패: " + response.statusCode())))
                    .bodyToMono(NaverLoginResponseDTO.NaverUserInfo.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("네이버 사용자 정보 조회에 실패했습니다", e);
        }
    }
}