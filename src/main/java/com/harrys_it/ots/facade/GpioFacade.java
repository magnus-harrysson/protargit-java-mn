package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.service.*;
import jakarta.inject.Singleton;

@Singleton
public class GpioFacade {
    private final Gpio1Service gpio1;
    private final Gpio2Service gpio2;
    private final Gpio3Service gpio3;
    private final Gpio4Service gpio4;

    public GpioFacade(Gpio1Service gpio1,
                      Gpio2Service gpio2,
                      Gpio3Service gpio3,
                      Gpio4Service gpio4) {
        this.gpio1 = gpio1;
        this.gpio2 = gpio2;
        this.gpio3 = gpio3;
        this.gpio4 = gpio4;
    }

    public void setGpio(int pin, int time){
        switch (pin){
            case 1: gpio1.set(time); break;
            case 2: gpio2.set(time); break;
            case 3: gpio3.set(time); break;
            case 4: gpio4.set(time); break;
            default: break;
        }
    }
}
