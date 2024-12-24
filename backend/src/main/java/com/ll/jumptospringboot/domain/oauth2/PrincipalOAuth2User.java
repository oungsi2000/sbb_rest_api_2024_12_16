package com.ll.jumptospringboot.domain.oauth2;

import com.ll.jumptospringboot.domain.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;

@Component
@Lazy
public class PrincipalOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Autowired
    private UserRepository userRepository;

    public PrincipalOAuth2User(OAuth2User oAuth2User) {
        this.authorities = oAuth2User.getAuthorities();
        this.attributes = oAuth2User.getAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return userRepository.findByEmail((String) attributes.get("email")).get().getUsername(); // 원하는 username 반환
    }
}