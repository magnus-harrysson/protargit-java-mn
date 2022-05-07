package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BatteryLedService {

    private final OsService osService;
    private final McuService mcuService;
    private volatile int volt;

    private static final Logger log = LoggerFactory.getLogger(BatteryLedService.class);

    public BatteryLedService(OsService osService, McuService mcuService, @Value("${hardware.services.enable}") boolean startService){
        this.osService = osService;
        this.mcuService = mcuService;
        if(startService) {
            log.debug("Started");
            run();
        }
    }

    private void run() {
        new Thread(() -> {
            while (true) {
                updateLed();
                threadSleep();
            }
        }).start();
    }

    private void updateLed() {
        try {
            volt = mcuService.writeSerial(new McuEvent(McuCommand.BATTERY_VOLTAGE, McuDataLimit.ANY_DATA.getValue()));
            calculateLedColor(volt);
        } catch (InterruptedException e) {
            log.debug("run() error in pcbToMcu.send() ", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Green. At least 29,00 V (2900)
     * Orange. Between 26,00-28,99 V (2600-2899)
     * Red. Less than 26,00 V (0-2600)
     * No light. Error code from MCU.
     */
    private void calculateLedColor(Integer voltageFromMcu) throws InterruptedException {
        if (voltageFromMcu>=2900) {
            osService.command(Os.BATTERY_LED_GREEN_HIGH);
            osService.command(Os.BATTERY_LED_RED_LOW);
        } else if (voltageFromMcu>=2600) {
            osService.command(Os.BATTERY_LED_GREEN_HIGH);
            osService.command(Os.BATTERY_LED_RED_HIGH);
        } else if (voltageFromMcu >= 0) {
            osService.command(Os.BATTERY_LED_GREEN_LOW);
            osService.command(Os.BATTERY_LED_RED_HIGH);
        } else {
            osService.command(Os.BATTERY_LED_GREEN_LOW);
            osService.command(Os.BATTERY_LED_RED_LOW);
        }
    }

    /** Sleep 5 minutes */
    private void threadSleep() {
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getVoltage() {
        return Math.max(volt, 0);
    }
}
