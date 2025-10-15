package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Value("${app.url.userInfo}")
    private String userUrl;

    @Value("${app.url.admin}")
    private String adminUrl;

    @Value("${app.url.default}")
    private String defaultUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect(adminUrl);
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect(userUrl);
        } else {
            response.sendRedirect(defaultUrl);
        }
    }
}