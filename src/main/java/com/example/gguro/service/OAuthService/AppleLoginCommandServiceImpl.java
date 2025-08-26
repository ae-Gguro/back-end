package com.example.gguro.service.OAuthService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gguro.apiPayload.code.status.ErrorStatus;
import com.example.gguro.apiPayload.exception.handler.AppleLoginHandler;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    public UserResponseDTO.UserLoginResponseDTO appleLogin(String code) {
        AppleUserInfoResponse userInfo = getAppleUserInfo(code);

        String oauthId = userInfo.getSub();
        String email = userInfo.getEmail();

        Optional<User> optionalUser = userRepository.findByOauthId(oauthId);

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // 신규 유저 생성 시 Apple은 이름을 주지 않음 → 기본 닉네임
            String nickname = (email != null) ? email.split("@")[0] : "사용자";
            user = UserConverter.toUserWithOauthId(oauthId, email, nickname, SocialType.APPLE);
            userRepository.save(user);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    private AppleUserInfoResponse getAppleUserInfo(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // clientSecret 잘만들어지는지 확인
        String clientSecret = generateClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

        System.out.println("Client Secret: " + clientSecret);
        log.debug("Client secret generated successfully");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<AppleSocialTokenInfoResponse> response;
        try {
            response = new RestTemplate().exchange(
                    APPLE_URL + "/auth/token",
                    HttpMethod.POST,
                    request,
                    AppleSocialTokenInfoResponse.class
            );
        } catch (Exception e) {
            throw new AppleLoginHandler(ErrorStatus.APPLE_AUTH_CODE_INVALID);
        }

        AppleSocialTokenInfoResponse tokenInfo = response.getBody();
        if (tokenInfo == null || tokenInfo.getIdToken() == null) {
            throw new AppleLoginHandler(ErrorStatus.APPLE_ID_TOKEN_MISSING);
        }

        try {
            DecodedJWT jwt = JWT.decode(tokenInfo.getIdToken());

            return UserConverter.toAppleUserInfo(jwt);
        } catch (Exception e) {
            throw new AppleLoginHandler(ErrorStatus.APPLE_ID_TOKEN_PARSE_FAIL);
        }
    }

    private String generateClientSecret() {
        log.info("👉 generateClientSecret() 진입. clientId={}, teamId={}, keyId={}", clientId, teamId, keyId);

        try {
            LocalDateTime exp = LocalDateTime.now().plusMinutes(5);

            String token = Jwts.builder()
                    .setHeaderParam(JwsHeader.KEY_ID, keyId)
                    .setIssuer(teamId)
                    .setAudience(APPLE_URL)
                    .setSubject(clientId)
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant()))
                    .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                    .compact();

            log.info("✅ Client Secret 생성 성공 (앞 30자): {}", token.substring(0, Math.min(30, token.length())));
            return token;
        } catch (Exception e) {
            e.printStackTrace(); // JVM 표준 출력
            log.error("❌ Apple Client Secret 생성 중 오류 발생", e); // logback 로그
            throw new AppleLoginHandler(ErrorStatus.APPLE_CLIENT_SECRET_GENERATION_FAIL);
        }
    }

    private PrivateKey getPrivateKey() {
        log.info("👉 getPrivateKey() 실행됨. Resource={}", privateKeyResource);

        try (InputStream inputStream = privateKeyResource.getInputStream()) {
            String pem = new String(inputStream.readAllBytes());
            log.info("🔑 Raw Private Key (앞 50자): {}", pem.substring(0, Math.min(50, pem.length())));

            pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\r", "")
                    .replaceAll("\\n", "")
                    .trim();

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace(); // JVM 표준 출력
            log.error("❌ Apple private key parse failed", e);
            throw new AppleLoginHandler(ErrorStatus.APPLE_PRIVATE_KEY_PARSE_FAIL);
        }
    }
}