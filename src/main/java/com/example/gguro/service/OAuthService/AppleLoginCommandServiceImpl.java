package com.example.gguro.service.OAuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gguro.converter.UserConverter;
import com.example.gguro.domain.User;
import com.example.gguro.domain.enums.SocialType;
import com.example.gguro.jwt.TokenProvider;
import com.example.gguro.repository.UserRepository;
import com.example.gguro.web.dto.TokenDTO;
import com.example.gguro.web.dto.UserResponseDTO;
import com.example.gguro.web.dto.apple.AppleSocialTokenInfoResponse;
import com.example.gguro.web.dto.apple.AppleUserInfoResponse;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppleLoginCommandServiceImpl implements AppleLoginCommandService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.redirect-uri}")
    private String redirectUri;

    @Value("${apple.private-key-path}")
    private Resource privateKeyResource;

    private final String APPLE_URL = "https://appleid.apple.com";

    @Override
    public UserResponseDTO.UserLoginResponseDTO appleLogin(String code) throws IOException {
        AppleUserInfoResponse userInfo = getAppleUserInfo(code);

        String oauthId = userInfo.getSub();
        String nickname = userInfo.getName() != null ? userInfo.getName() : "사용자";
        String email = userInfo.getEmail();  // null일 수도 있음 (hide email 옵션일 경우)

        Optional<User> optionalUser = userRepository.findByOauthId(oauthId);
        User user = optionalUser.orElseGet(() -> {
            User newUser = UserConverter.toUserWithOauthId(oauthId, email, nickname, SocialType.APPLE);
            return userRepository.save(newUser);
        });

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    private AppleUserInfoResponse getAppleUserInfo(String code) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + generateClientSecret() +
                "&code=" + code +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + redirectUri;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<AppleSocialTokenInfoResponse> response = new RestTemplate().exchange(
                APPLE_URL + "/auth/token",
                HttpMethod.POST,
                request,
                AppleSocialTokenInfoResponse.class
        );

        DecodedJWT jwt = JWT.decode(Objects.requireNonNull(response.getBody()).getIdToken());

        return AppleUserInfoResponse.builder()
                .sub(jwt.getClaim("sub").asString())
                .name(jwt.getClaim("name") != null ? jwt.getClaim("name").asString() : null)
                .email(jwt.getClaim("email") != null ? jwt.getClaim("email").asString() : null)
                .build();
    }

    private String generateClientSecret() {
        LocalDateTime exp = LocalDateTime.now().plusMinutes(5);
        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, keyId)
                .setIssuer(teamId)
                .setAudience(APPLE_URL)
                .setSubject(clientId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try (InputStream inputStream = privateKeyResource.getInputStream()) {
            String pem = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Apple 비공개키 파싱 실패", e);
        }
    }
}