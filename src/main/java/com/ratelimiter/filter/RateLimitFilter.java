package com.ratelimiter.filter;

import com.ratelimiter.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);

        if (!rateLimiterService.isAllowed(clientIp)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Rate limit exceeded. Max 100 requests/min.\", " +
                "\"retryAfter\": 60}"
            );
            return;
        }

        response.setHeader("X-RateLimit-Remaining",
            String.valueOf(rateLimiterService.getRemainingRequests(clientIp)));

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
