package com.example.eventjava;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        System.out.println("Starting Micronaut application...");
        Micronaut.run(Application.class, args);
    }
}
