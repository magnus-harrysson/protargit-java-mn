package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import jakarta.inject.Singleton;

@Singleton
public class Gpio3Service extends GpioBase {
    public Gpio3Service(OsService osService) {
        super(osService, Os.GPIO_3_LOW, Os.GPIO_3_HIGH);
    }
}
