package com.cms.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        String redirectUrl = null;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authorityName.equals("ROLE_STUDENT")) {
                redirectUrl = "/student/dashboard";
                break;
            } else if (authorityName.equals("ROLE_TEACHER")) {
                // Assuming a teacher dashboard will be created later
                redirectUrl = "/teacher/dashboard";
                break;
            }
        }

        if (redirectUrl == null) {
            throw new IllegalStateException("Could not determine user role for redirection.");
        }

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}

