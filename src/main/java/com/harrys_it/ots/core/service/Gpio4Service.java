package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class Gpio4Service extends GpioBase {
    public Gpio4Service(OsService osService, @Value("${hardware.services.enable}") boolean startService) {
        super(osService, Os.GPIO_4_LOW, Os.GPIO_4_HIGH, startService);
    }
}
