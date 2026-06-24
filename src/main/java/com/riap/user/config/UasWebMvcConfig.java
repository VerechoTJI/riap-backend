package com.riap.user.config;

import com.riap.user.security.AuthInterceptor;
import com.riap.user.security.JwtService;
import com.riap.user.security.TokenBlacklist;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers the UAS {@link AuthInterceptor} so {@link com.riap.user.security.RequireRole}
 * is enforced across MVC controllers. The interceptor is a no-op for endpoints that are
 * not annotated, so other subsystems' open endpoints are unaffected.
 *
 * <p>The interceptor is built here (not as a bean) from {@link ObjectProvider}-resolved
 * collaborators, so sliced web tests ({@code @WebMvcTest}) — which do not load the UAS
 * {@code @Service}/{@code @Component} beans — can still build their MVC context; RBAC is
 * simply not registered there.
 */
@Configuration
public class UasWebMvcConfig implements WebMvcConfigurer {

    private final ObjectProvider<JwtService> jwtService;
    private final ObjectProvider<TokenBlacklist> tokenBlacklist;

    public UasWebMvcConfig(ObjectProvider<JwtService> jwtService, ObjectProvider<TokenBlacklist> tokenBlacklist) {
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        JwtService jwt = jwtService.getIfAvailable();
        TokenBlacklist blacklist = tokenBlacklist.getIfAvailable();
        if (jwt != null && blacklist != null) {
            registry.addInterceptor(new AuthInterceptor(jwt, blacklist));
        }
    }
}
