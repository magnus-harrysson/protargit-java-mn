package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.service.*;
import jakarta.inject.Singleton;

@Singleton
@SuppressWarnings("java:S6206")
public class GpioFacade {

    private final Gpio1Service gpio1;
    private final Gpio2Service gpio2;
    private final Gpio3Service gpio3;
    private final Gpio4Service gpio4;

    public GpioFacade(Gpio1Service gpio1, Gpio2Service gpio2, Gpio3Service gpio3, Gpio4Service gpio4) {
        this.gpio1 = gpio1;
        this.gpio2 = gpio2;
        this.gpio3 = gpio3;
        this.gpio4 = gpio4;
    }

    public Gpio1Service gpio1() {
        return gpio1;
    }

    public Gpio2Service gpio2() {
        return gpio2;
    }

    public Gpio3Service gpio3() {
        return gpio3;
    }

    public Gpio4Service gpio4() {
        return gpio4;
    }

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
