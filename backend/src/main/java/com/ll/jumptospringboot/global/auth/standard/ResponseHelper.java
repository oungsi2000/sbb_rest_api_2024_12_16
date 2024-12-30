package com.ll.jumptospringboot.global.auth.standard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ResponseHelper {

    @Value("${jwt.expiration_time}")
    Long expirationTime;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Cookie setCookie(String token) {
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(expirationTime));
        return cookie;
    }

    public PrintWriter setResponse(HttpServletResponse response) throws IOException {
        AuthResponse authResponse = new AuthResponse("성공", HttpServletResponse.SC_OK);
        String jsonString = objectMapper.writeValueAsString(authResponse); // DTO를 JSON 문자열로 변환
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(jsonString);
        return writer;
    }
}
