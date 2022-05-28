package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GpioBase {
    private final OsService osService;
    private boolean isHigh = false;
    private Integer time = null;
    private final Os gpioLow;
    private final Os gpioHigh;
    private static final Logger log = LoggerFactory.getLogger(GpioBase.class);

    protected GpioBase(OsService osService, Os gpioLow, Os gpioHigh, boolean startService) {
        this.osService = osService;
        this.gpioLow = gpioLow;
        this.gpioHigh = gpioHigh;
        if(startService) {
            log.debug("Started");
            run();
        }
    }

    private void run() {
        new Thread(() -> {
            while (true) {
                if(time!= null) {
                    handleGpio();
                }
                threadSleep(2);
            }
        }).start();
    }

    private void handleGpio() {
        if (time == 0) {
            setGpio(false, gpioLow);
        }
        if (time == 1) {
            setGpio(true, gpioHigh);
        }
        if (time > 1) {
            setGpio(true, gpioHigh);
            threadSleep(time);
            setGpio(false, gpioLow);
        }
        if (time < 0) {
            setGpio(false, gpioLow);
            threadSleep(time);
            setGpio(true, gpioHigh);
        }
        time = null;
    }

    private void setGpio(boolean isHigh, Os state) {
        try {
            this.isHigh = isHigh;
            osService.command(state);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            time = null;
            run();
        }
    }

    /**
     * Set time the port should be in high.
     * time=0: OFF
     * time=1: ON
     * time>1: High for "time" in milliseconds
     * time<0: Low for "time" in milliseconds
     * @param time in milliseconds
     */
    public boolean set(int time) {
        this.time = time;
        return true;
    }

    public boolean isHigh() {
        return isHigh;
    }

    private void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.debug("threadSleep", e);
            Thread.currentThread().interrupt();
        }
    }

}
