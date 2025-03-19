package com.example.Wit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    public Long getUserIdFromToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }
        token = token.substring(7); // "Bearer " 제거

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
}
