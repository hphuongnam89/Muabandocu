package com.cho2hand.marketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final long WINDOW_MILLIS = 60_000;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        int limit = limit(request);
        if (limit > 0) {
            var key = clientIp(request) + ':' + request.getRequestURI();
            var window = windows.compute(key, (ignored, current) -> current == null || current.startedAt + WINDOW_MILLIS < System.currentTimeMillis()
                    ? new Window(System.currentTimeMillis(), 1) : new Window(current.startedAt, current.requests + 1));
            if (window.requests > limit) {
                response.setStatus(429);
                response.setContentType("application/problem+json");
                response.getWriter().write("{\"status\":429,\"detail\":\"Too many requests. Please try again shortly.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private int limit(HttpServletRequest request) {
        var path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/")) return 10;
        if ("POST".equals(request.getMethod()) && (path.contains("/messages") || path.contains("/reports") || path.equals("/api/v1/listings"))) return 60;
        return 0;
    }

    private String clientIp(HttpServletRequest request) {
        var forwarded = request.getHeader("X-Forwarded-For");
        return forwarded == null || forwarded.isBlank() ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
    }

    // ponytail: in-memory limits cover one container; replace with Redis only when multiple backend replicas are introduced.
    private record Window(long startedAt, int requests) { }
}
