package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.model.TargetStatusBroadcastMessage;
import com.harrys_it.ots.core.service.*;
import jakarta.inject.Singleton;

@Singleton
public class TargetStatusFacade {

    private final GpioFacade gpioFacade;
    private final ZonesService zones;
    private final MechanicalService mechanical;
    private final BatteryLedService batteryLed;
    private final SerialProtocolService serialProtocolService;

    public TargetStatusFacade(GpioFacade gpioFacade,
                              ZonesService zones,
                              MechanicalService mechanical,
                              BatteryLedService batteryLed,
                              SerialProtocolService serialProtocolService) {
        this.gpioFacade = gpioFacade;
        this.zones = zones;
        this.mechanical = mechanical;
        this.batteryLed = batteryLed;
        this.serialProtocolService = serialProtocolService;
    }

    public TargetStatusBroadcastMessage getStatus() {
        return new TargetStatusBroadcastMessage(
            new boolean[]{
                gpioFacade.gpio1().isHigh(),
                gpioFacade.gpio2().isHigh(),
                gpioFacade.gpio3().isHigh(),
                gpioFacade.gpio4().isHigh(),
            },
            zones.isZonesActive(),
            serialProtocolService.isGpsEnable(),
            batteryLed.getVoltage(),
            mechanical.getTwistMotorSpeed(),
            mechanical.getTwistMotorCurrentAngle(),
            mechanical.getFlipMotorSpeed(),
            mechanical.getFlipMotorCurrentAngle()
        );
    }
}
