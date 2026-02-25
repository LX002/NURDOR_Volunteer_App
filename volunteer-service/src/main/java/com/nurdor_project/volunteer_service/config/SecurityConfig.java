package com.nurdor_project.volunteer_service.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private CustomAuthenticationSuccessHandler customLoginSuccessHandler;
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/volunteer/**").hasRole("VOLUNTEER")
                        .requestMatchers("/login", "/register", "/api/guest/**", "/api/welcome").permitAll()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        //.defaultSuccessUrl("/api/welcome", true)
                        //.defaultSuccessUrl("/welcome", true)
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
