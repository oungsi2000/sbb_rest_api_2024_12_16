package com.ll.jumptospringboot.global.auth.oauth2;

import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRepository;
import com.ll.jumptospringboot.domain.User.UserRole;
import com.ll.jumptospringboot.domain.User.UserService;
import com.ll.jumptospringboot.global.auth.dto.UserCreateOauthDto;
import com.ll.jumptospringboot.global.auth.standard.ResponseHelper;
import com.ll.jumptospringboot.global.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ResponseHelper responseHelper;

    private void setToken(HttpServletResponse response, Authentication auth, UserRole role) throws IOException {
        String token = jwtProvider.generateOauthToken(auth, role);
        response.addCookie(responseHelper.setCookie(token));

        try (PrintWriter writer = responseHelper.setResponse(response)) {
            if (role == UserRole.TEMPORARY_USER) {
                response.sendRedirect("http://localhost:3000/signup-oauth");
            } else {
                response.sendRedirect("http://localhost:3000/");
            }
            writer.flush();
        }
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
        String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        Optional<SiteUser> user = userRepository.findByEmail(email);

        //사용자 정보가 없을 때 회원가입
        if (user.isEmpty()) {
            UserCreateOauthDto userCreateOauthDto = new UserCreateOauthDto();
            userCreateOauthDto.setEmail(email);
            userCreateOauthDto.setProviderId(registrationId);
            userCreateOauthDto.setOauthId(googleId);
            userService.createOauth(userCreateOauthDto);

            setToken(response, auth, UserRole.TEMPORARY_USER);
            return;
        }
        if (user.get().getUsername() == null) {
            setToken(response, auth, UserRole.TEMPORARY_USER);
        } else {
            setToken(response, auth, UserRole.USER);
        }
    }
}
