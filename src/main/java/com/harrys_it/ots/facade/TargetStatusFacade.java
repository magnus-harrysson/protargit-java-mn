package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.model.TargetStatusBroadcastMessage;
import com.harrys_it.ots.core.service.*;
import jakarta.inject.Singleton;

@Singleton
public class TargetStatusFacade {

    private final Gpio1Service gpio1;
    private final Gpio2Service gpio2;
    private final Gpio3Service gpio3;
    private final Gpio4Service gpio4;
    private final ZonesService zones;
    private final MechanicalService mechanical;
    private final BatteryLedService batteryLed;
    private final SerialProtocolService serialProtocolService;

    public TargetStatusFacade(Gpio1Service gpio1,
                              Gpio2Service gpio2,
                              Gpio3Service gpio3,
                              Gpio4Service gpio4,
                              ZonesService zones,
                              MechanicalService mechanical,
                              BatteryLedService batteryLed,
                              SerialProtocolService serialProtocolService) {
        this.gpio1 = gpio1;
        this.gpio2 = gpio2;
        this.gpio3 = gpio3;
        this.gpio4 = gpio4;
        this.zones = zones;
        this.mechanical = mechanical;
        this.batteryLed = batteryLed;
        this.serialProtocolService = serialProtocolService;
    }

    public TargetStatusBroadcastMessage getStatus() {
        return new TargetStatusBroadcastMessage(
            new boolean[]{
                gpio1.isHigh(),
                gpio2.isHigh(),
                gpio3.isHigh(),
                gpio4.isHigh(),
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
