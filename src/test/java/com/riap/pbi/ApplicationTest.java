package com.riap.pbi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.riap.RiapApplication;

class ApplicationTest {

    @Test
    void messageReturnsReadyState() {
        assertEquals("RIAP Maven project is ready.", RiapApplication.message());
    }
}