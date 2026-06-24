package com.riap.user.security;

import com.riap.user.domain.model.UserRole;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Enforces {@link RequireRole} on annotated handlers (UAS-F-02, UAS-F-04). Endpoints
 * without the annotation are left open, so existing LMS/RCS controllers are unaffected.
 *
 * <ul>
 *   <li>missing / invalid / expired / revoked token → 401 Unauthorized</li>
 *   <li>valid token but role not permitted → 403 Forbidden</li>
 *   <li>otherwise the {@link AuthenticatedUser} is exposed via a request attribute</li>
 * </ul>
 */
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return true; // endpoint is not protected
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        AuthenticatedUser user;
        try {
            user = jwtService.parse(header.substring(BEARER_PREFIX.length()));
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (tokenBlacklist.isRevoked(user.tokenId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserRole[] allowed = requireRole.value();
        if (allowed.length > 0 && Arrays.stream(allowed).noneMatch(r -> r == user.role())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        request.setAttribute(AuthenticatedUser.ATTRIBUTE, user);
        return true;
    }
}
