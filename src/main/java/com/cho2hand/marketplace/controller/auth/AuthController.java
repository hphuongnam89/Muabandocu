package com.cho2hand.marketplace.controller.auth;

import com.cho2hand.marketplace.dto.auth.AuthResponse;
import com.cho2hand.marketplace.dto.auth.ChangePasswordRequest;
import com.cho2hand.marketplace.dto.auth.LoginRequest;
import com.cho2hand.marketplace.dto.auth.PasswordResetRequest;
import com.cho2hand.marketplace.dto.auth.RegisterRequest;
import com.cho2hand.marketplace.dto.auth.ResetPasswordRequest;
import com.cho2hand.marketplace.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) { return issue(authService.register(request), response); }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) { return issue(authService.login(request), response); }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) { var cookie = new Cookie("OLDMARKET_TOKEN", ""); cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/"); cookie.setMaxAge(0); response.addCookie(cookie); }

    private AuthResponse issue(AuthResponse auth, HttpServletResponse response) { var cookie = new Cookie("OLDMARKET_TOKEN", auth.accessToken()); cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/"); cookie.setMaxAge(60 * 60); response.addCookie(cookie); return auth; }

    @PostMapping("/password-reset-requests")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) { authService.requestPasswordReset(request); }

    @PostMapping("/password-resets")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) { authService.resetPassword(request); }

    @PostMapping("/password-changes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Long userId, @Valid @RequestBody ChangePasswordRequest request) { authService.changePassword(userId, request); }
}
