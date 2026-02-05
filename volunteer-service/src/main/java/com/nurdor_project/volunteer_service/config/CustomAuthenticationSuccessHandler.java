package com.nurdor_project.volunteer_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = request.getContextPath();
//        if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
//            redirectUrl = "/admin/welcome";
//        } else if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_VOLUNTEER"))) {
//            redirectUrl = "/volunteer/welcome";
//        }

        response.sendRedirect(redirectUrl);
    }
}
