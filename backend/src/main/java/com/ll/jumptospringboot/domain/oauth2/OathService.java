package com.ll.jumptospringboot.domain.oauth2;


import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRepository;
import com.ll.jumptospringboot.exception.UserDuplicateException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OathService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String email = (String) oAuth2User.getAttributes().get("email");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Optional<SiteUser> user = userRepository.findByEmail(email);

        //DB수준에서도 막고 (유니크 키)서비스 단에서도 이미 다른 이메일로 가입했다고 알림
        if (user.isPresent()) {
            if (!Objects.equals(user.get().getProviderId(), registrationId)) {
                throw new UserDuplicateException("이미 다른 이메일로 가입하셨습니다");
            }
        }

        Authentication authentication = new OAuth2AuthenticationToken(oAuth2User, null, registrationId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return oAuth2User;
    }
}
