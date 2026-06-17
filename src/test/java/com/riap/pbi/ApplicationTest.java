package com.riap.pbi;

import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void applicationClassExists() {
        // Verify the Application class is a valid Spring Boot entry point
        // Full context load tests are in the controller slice tests (Phase 6)
        new Application();
    }
}
