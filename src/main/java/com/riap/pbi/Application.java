package com.riap.pbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private Application() {
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static String message() {
        return "RIAP Maven project is ready.";
    }
}