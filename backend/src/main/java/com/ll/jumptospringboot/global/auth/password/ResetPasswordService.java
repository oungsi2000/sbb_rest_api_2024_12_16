package com.ll.jumptospringboot.global.auth.password;

import com.ll.jumptospringboot.domain.User.SiteUser;
import com.ll.jumptospringboot.domain.User.UserRepository;
import com.ll.jumptospringboot.global.auth.dto.AuthResponse;
import com.ll.jumptospringboot.global.exception.PasswordNotSameException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    @Async
    protected void mailSender(String email, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("임시 비밀번호 발송");
        message.setText("임시 비밀번호: " + tempPassword); //암호화 하기 전 임시 비밀번호를 보내야 함
        mailSender.send(message);
    }

    public ResponseEntity<AuthResponse> generateTempPassword(String email){
        Optional<SiteUser> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new UsernameNotFoundException("유저를 찾을 수 없습니다");
        String password = UUID.randomUUID().toString().substring(0, 8);
        user.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(user.get());

        mailSender(email, password);
        AuthResponse authResponse = new AuthResponse("성공", HttpServletResponse.SC_OK);

        return ResponseEntity.ok().body(authResponse);
    }

    public ResponseEntity<AuthResponse> changePassword(String password, String newPassword, SiteUser user) {
        if (!passwordEncoder.matches(password, user.getPassword())) throw new PasswordNotSameException("비밀번호가 일치하지 않습니다");
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse("성공", HttpServletResponse.SC_OK);
        return ResponseEntity.ok().body(authResponse);
    }
}
