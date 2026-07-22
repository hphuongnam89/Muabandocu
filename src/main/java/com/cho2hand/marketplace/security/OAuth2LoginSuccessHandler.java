package com.cho2hand.marketplace.security;

import com.cho2hand.marketplace.service.auth.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthService authService;
    private final String frontendUrl;
    public OAuth2LoginSuccessHandler(AuthService authService, @Value("${app.frontend-url:http://localhost:8088}") String frontendUrl) { this.authService = authService; this.frontendUrl = frontendUrl; }
    @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var user = (OAuth2User) authentication.getPrincipal();
        var auth = authService.oauthLogin("GOOGLE", user.getAttribute("sub"), user.getAttribute("email"), user.getAttribute("name"));
        var cookie = new Cookie("OLDMARKET_TOKEN", auth.accessToken()); cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/"); cookie.setMaxAge(3600); response.addCookie(cookie);
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/oauth-success");
    }
}
