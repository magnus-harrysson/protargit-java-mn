package com.harrys_it.ots;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        // Eager init of singletons because of singleton threads architecture.
        Micronaut.build(args)
                .eagerInitSingletons(true)
                .mainClass(Application.class)
                .start();
    }
}
