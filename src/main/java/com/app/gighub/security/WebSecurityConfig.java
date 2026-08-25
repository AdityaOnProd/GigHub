package com.app.gighub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/favicon.ico").permitAll()
                .requestMatchers("/login", "/register", "/job", "/job/view/*").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .failureUrl("/login-error")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return httpSecurity.build();
    }
}
