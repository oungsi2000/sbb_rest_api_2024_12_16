package com.ll.jumptospringboot.global.auth.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class FormLoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        AuthResponse authResponse = new AuthResponse(exception.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        String jsonString = objectMapper.writeValueAsString(authResponse); // DTO를 JSON 문자열로 변환
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(jsonString);
        writer.flush();

    }
}
