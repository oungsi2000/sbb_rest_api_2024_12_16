package com.ll.jumptospringboot.global.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 생성

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        if (exception instanceof OAuth2AuthenticationException) {
            AuthResponse authResponse = new AuthResponse(exception.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
            String jsonString = objectMapper.writeValueAsString(authResponse); // DTO를 JSON 문자열로 변환
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonString);
            response.sendRedirect("http://localhost:3000/");

        }

    }
}