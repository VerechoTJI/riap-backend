package com.riap.pbi;

public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        System.out.println(message());
    }

    public static String message() {
        return "RIAP Maven project is ready.";
    }
}