package com.ll.jumptospringboot.util;

import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.global.auth.dto.UserContextDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.expiration_time}")
    Long expirationTime;

    public String generateFormToken(Authentication authentication, UserRole role) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // JWT 생성
        return Jwts.builder()
            .subject(username) // JWT에 담을 정보 (예: 사용자 ID)
            .issuedAt(new Date())
            .claim("role", role)
            .expiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
            .signWith(key) // 서명 알고리즘과 비밀 키
            .compact();
    }

    public String regenerateToken(String username, UserRole role) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
            .subject(username) // JWT에 담을 정보 (예: 사용자 ID)
            .issuedAt(new Date())
            .claim("role", role)
            .expiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
            .signWith(key) // 서명 알고리즘과 비밀 키
            .compact();
    }
    public String generateOauthToken(Authentication authentication, UserRole role) {
        String sub = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // JWT 생성
        return Jwts.builder()
            .subject(sub) // JWT에 담을 정보 (예: 사용자 ID)
            .issuedAt(new Date())
            .claim("role", role)
            .expiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
            .signWith(key) // 서명 알고리즘과 비밀 키
            .compact();
    }

    public UserContextDto validate(String token) throws ValidationException{
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        UserContextDto userContextDto = new UserContextDto();
        try {

            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            Date now = new Date();

            if (claims.getExpiration().before(now)) {
                throw new ValidationException("유효기간이 지났습니다");
            }

            userContextDto.setName(claims.getSubject());
            userContextDto.setRole(UserRole.valueOf(claims.get("role", String.class)));
            return userContextDto;

        } catch (Exception e) {
            throw new ValidationException("유효하지 않은 토큰입니다");
        }
    }
}
