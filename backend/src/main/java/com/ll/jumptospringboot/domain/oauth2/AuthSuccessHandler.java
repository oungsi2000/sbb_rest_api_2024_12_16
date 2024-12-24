package com.ll.jumptospringboot.domain.oauth2;

import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserCreateForm;
import com.ll.jumptospringboot.domain.User.UserRepository;
import com.ll.jumptospringboot.domain.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
        String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        Optional<SiteUser> user = userRepository.findByEmail(email);
        HttpSession session = request.getSession();

        //사용자 정보가 없을 때 회원가입
        if (user.isEmpty()) {
            UserCreateOauthDto userCreateOauthDto = new UserCreateOauthDto();
            userCreateOauthDto.setEmail(email);
            userCreateOauthDto.setProviderId(registrationId);
            userCreateOauthDto.setOauthId(googleId);
            userService.createOauth(userCreateOauthDto);

            session.setAttribute("isFulfilled", false);
            response.sendRedirect("/oauth-signup");
            return;
        }
        if (user.get().getUsername() == null) {
            session.setAttribute("isFulfilled", false);
            response.sendRedirect("/oauth-signup");

        } else {
            response.sendRedirect("/");

        }
    }
}
