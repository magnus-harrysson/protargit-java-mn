package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ButtonLedService {
    private final OsService osService;
    private volatile int blinkInterval;
    private ButtonLedState state = ButtonLedState.OFF;
    private static final Logger log = LoggerFactory.getLogger(ButtonLedService.class);

    public ButtonLedService(OsService osService, @Value("${start.services:true}") boolean startService){
        this.osService = osService;
        if(startService){
            log.debug("{}", ButtonLedService.class.getName() + " Started");
            run();
        }
    }

    private void run() {
        new Thread(() -> {
            while (true) {
                try {
                    threadSleep(50);
                    switch (state) {
                        case OFF -> osService.command(Os.SYS_LED_OFF);
                        case ON -> osService.command(Os.SYS_LED_ON);
                        case SWITCHING -> {
                            osService.command(Os.SYS_LED_ON);
                            threadSleep(blinkInterval);
                            osService.command(Os.SYS_LED_OFF);
                            threadSleep(blinkInterval);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    /**
     * Button led blink interval
     * @param blinkInterval 0=off, 1=on, blinkInterval>1=switching.
     */
    public void setBlinkInterval(int blinkInterval) {
        if(blinkInterval < 0 ) {
            throw new NumberFormatException("Must be a positive value");
        }

        if(blinkInterval == 0) {
            this.state = ButtonLedState.OFF;
        } else {
            this.state = blinkInterval == 1 ? ButtonLedState.ON : ButtonLedState.SWITCHING;
        }

        this.blinkInterval = blinkInterval;
    }

    private void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public enum ButtonLedState {
        ON,
        OFF,
        SWITCHING
    }
}
