package com.example.gguro.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 안전한 키 생성
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("🔐 JWT Key: " + base64Key);
    }
}