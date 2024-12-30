package com.ll.jumptospringboot.global.auth.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import com.ll.jumptospringboot.global.auth.standard.ResponseHelper;
import com.ll.jumptospringboot.util.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${jwt.expiration_time}")
    Long expirationTime;
    @Autowired
    private ResponseHelper responseHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        String token = jwtProvider.generateFormToken(auth, UserRole.USER);
        response.addCookie(responseHelper.setCookie(token));

        try (PrintWriter writer = responseHelper.setResponse(response)) {
            response.sendRedirect("http://localhost:3000/");
            writer.flush();
        }

    }
}
