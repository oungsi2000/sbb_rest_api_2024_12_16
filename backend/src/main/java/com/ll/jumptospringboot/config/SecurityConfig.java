package com.ll.jumptospringboot.config;

import com.ll.jumptospringboot.global.auth.form.FormLoginFailureHandler;
import com.ll.jumptospringboot.global.auth.form.FormLoginSuccessHandler;
import com.ll.jumptospringboot.global.auth.oauth2.OAuthFailureHandler;
import com.ll.jumptospringboot.global.auth.oauth2.OAuthSuccessHandler;
import com.ll.jumptospringboot.global.auth.oauth2.OathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private OathService oathService; //

    @Autowired
    private OAuthFailureHandler OAuthFailureHandler;

    @Autowired
    private OAuthSuccessHandler authSuccessHandler;

    @Autowired
    private FormLoginFailureHandler formLoginFailureHandler;

    @Autowired
    private FormLoginSuccessHandler formLoginSuccessHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())

                .formLogin((formLogin) -> formLogin
                    .loginProcessingUrl("/api/v1/login")
                    .failureHandler(formLoginFailureHandler)
                    .successHandler(formLoginSuccessHandler)

                ).logout((logout) -> logout
                    .logoutUrl("/api/v1/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "Authorization")

                ).oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:3000/")
                .loginPage("/login/oauth")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oathService)
                )
                .successHandler(authSuccessHandler)
                .failureHandler(OAuthFailureHandler)
                ).logout((logout) -> logout
                    .logoutUrl("/api/v1/logout")
                    .logoutSuccessUrl("http://localhost:3000/")
                    .deleteCookies("JSESSIONID", "Authorization")
            )
        ;
        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
