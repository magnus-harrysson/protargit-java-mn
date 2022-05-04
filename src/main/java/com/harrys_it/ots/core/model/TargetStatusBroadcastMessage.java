package com.harrys_it.ots.core.model;

public record TargetStatusBroadcastMessage(
        boolean[] gpioHigh,
        boolean zonesActive,
        boolean gpsEnable,
        int voltage,
        int twistMotorSpeed,
        int twistMotorCurrentAngle,
        int flipMotorSpeed,
        int flipMotorCurrentAngle
) {}
