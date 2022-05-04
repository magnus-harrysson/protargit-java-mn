package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import jakarta.inject.Singleton;

@Singleton
public class Gpio2Service extends GpioBase {
    public Gpio2Service(OsService osService) {
        super(osService, Os.GPIO_2_LOW, Os.GPIO_2_HIGH);
    }
}
