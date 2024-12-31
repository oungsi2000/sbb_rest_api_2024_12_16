package com.ll.jumptospringboot.domain.User;

import com.ll.jumptospringboot.global.auth.dto.UserCreateOauthDto;
import com.ll.jumptospringboot.global.exception.DataNotFoundException;
import com.ll.jumptospringboot.global.util.JwtProvider;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser createOauth(UserCreateOauthDto userCreateOauthDto) {
        Optional<SiteUser> user = userRepository.findByEmail(userCreateOauthDto.getEmail());
        if (user.isEmpty()) {
            SiteUser newUser = new SiteUser();
            newUser.setProviderId(userCreateOauthDto.getProviderId());
            newUser.setEmail(userCreateOauthDto.getEmail());
            newUser.setOauthId(userCreateOauthDto.getOauthId());
            userRepository.save(newUser);
            return newUser;
        } else {
            user.get().setUsername(userCreateOauthDto.getUsername());
            userRepository.save(user.get());

            return user.get();
        }
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            Optional<SiteUser> siteUserByOauth = this.userRepository.findByOauthId(username);
            if (siteUserByOauth.isPresent()) return siteUserByOauth.get();

            throw new DataNotFoundException("siteuser not found");
        }
    }


    public SiteUser getUserByEmail(String email) {
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
