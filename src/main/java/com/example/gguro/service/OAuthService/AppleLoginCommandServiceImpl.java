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
import com.example.gguro.web.dto.apple.AppleLoginRequest;
import com.example.gguro.web.dto.apple.AppleSocialTokenInfoResponse;
import com.example.gguro.web.dto.apple.AppleUserInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public UserResponseDTO.UserLoginResponseDTO appleLogin(String code, String userJson) {
        AppleUserInfoResponse userInfo = getAppleUserInfo(code);

        String oauthId = userInfo.getSub();
        String email = userInfo.getEmail();

        Optional<User> optionalUser = userRepository.findByOauthId(oauthId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
            return UserConverter.toUserLoginResponseDTO(tokenDTO);
        }

        String nickname = "사용자";
        if (userJson != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                AppleLoginRequest.AppleUser parsedUser = objectMapper.readValue(userJson, AppleLoginRequest.AppleUser.class);

                if (parsedUser.getName() != null) {
                    String first = parsedUser.getName().getFirstName();
                    String last = parsedUser.getName().getLastName();
                    nickname = ((last != null ? last : "") + (first != null ? first : "")).trim();
                }

            } catch (JsonProcessingException e) {
                log.warn("Apple user JSON 파싱 실패", e);
            }
        }

        User newUser = UserConverter.toUserWithOauthId(oauthId, email, nickname, SocialType.APPLE);
        userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser.getId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);
        return UserConverter.toUserLoginResponseDTO(tokenDTO);
    }

    private AppleUserInfoResponse getAppleUserInfo(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", generateClientSecret());
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

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
        try {
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
        } catch (Exception e) {
            throw new AppleLoginHandler(ErrorStatus.APPLE_CLIENT_SECRET_GENERATION_FAIL);
        }
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
            throw new AppleLoginHandler(ErrorStatus.APPLE_PRIVATE_KEY_PARSE_FAIL);
        }
    }
}