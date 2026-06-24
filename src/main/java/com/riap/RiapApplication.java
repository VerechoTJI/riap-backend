package com.riap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class RiapApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiapApplication.class, args);
    }

    public static String message() {
        return "RIAP Maven project is ready.";
    }
}
