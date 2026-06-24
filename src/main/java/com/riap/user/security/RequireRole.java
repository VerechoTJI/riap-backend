package com.riap.user.security;

import com.riap.user.domain.model.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method (or class) as requiring a valid UAS token (UAS-F-02,
 * UAS-F-04). {@link AuthInterceptor} enforces it: a missing/invalid/revoked token is
 * rejected with 401, and a token whose role is not in {@link #value()} with 403.
 *
 * <p>An empty {@link #value()} means "any authenticated user". Other subsystems annotate
 * their own endpoints with this to apply role-based access control.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    UserRole[] value() default {};
}
