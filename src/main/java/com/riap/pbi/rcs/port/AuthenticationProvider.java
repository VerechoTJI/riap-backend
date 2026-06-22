package com.riap.pbi.rcs.port;

public interface AuthenticationProvider {
    /**
     * Validates a token and returns the userId if successful.
     * @param token The authentication token.
     * @return The userId if the token is valid, otherwise null.
     */
    String validateTokenAndGetUserId(String token);
}
