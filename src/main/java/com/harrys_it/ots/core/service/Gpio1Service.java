package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import jakarta.inject.Singleton;

@Singleton
public class Gpio1Service extends GpioBase {
    public Gpio1Service(OsService osService) {
        super(osService, Os.GPIO_1_LOW, Os.GPIO_1_HIGH);
    }
}
