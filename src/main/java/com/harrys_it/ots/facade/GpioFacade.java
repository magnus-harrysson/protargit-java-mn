package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.service.*;
import jakarta.inject.Singleton;

@Singleton
public record GpioFacade(Gpio1Service gpio1, Gpio2Service gpio2, Gpio3Service gpio3, Gpio4Service gpio4) {

    public void setGpio(int pin, int time) {
        switch (pin) {
            case 1 -> gpio1.set(time);
            case 2 -> gpio2.set(time);
            case 3 -> gpio3.set(time);
            case 4 -> gpio4.set(time);
            default -> { /* do nothing */ }
        }
    }
}
