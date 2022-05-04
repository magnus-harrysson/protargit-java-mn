package com.harrys_it.ots.core.model.settings;

import java.util.Objects;

public class ManufactureSettings {
    private final int flipMotorMotorCurrentLimit;
    private final int flipMotorCalibrationMotorCurrentLimit;
    private final int flipMotorSpeed;
    private final int flipMotorCalibrationSpeed;
    private final int flipMotorHysteresis;

    private final int twistMotorMotorCurrentLimit;
    private final int twistMotorCalibrationMotorCurrentLimit;
    private final int twistMotorSpeed;
    private final int twistMotorCalibrationSpeed;
    private final int twistMotorHysteresis;

    private final int twistMotorAngleOffset;

    private final int targetId;

    @SuppressWarnings("squid:S107")
    public ManufactureSettings(int flipMotorMotorCurrentLimit,
                               int flipMotorCalibrationMotorCurrentLimit,
                               int flipMotorSpeed,
                               int flipMotorCalibrationSpeed,
                               int flipMotorHysteresis,
                               int twistMotorMotorCurrentLimit,
                               int twistMotorCalibrationMotorCurrentLimit,
                               int twistMotorSpeed,
                               int twistMotorCalibrationSpeed,
                               int twistMotorHysteresis,
                               int twistMotorAngleOffset,
                               int targetId) {
        this.flipMotorMotorCurrentLimit = flipMotorMotorCurrentLimit;
        this.flipMotorCalibrationMotorCurrentLimit = flipMotorCalibrationMotorCurrentLimit;
        this.flipMotorSpeed = flipMotorSpeed;
        this.flipMotorCalibrationSpeed = flipMotorCalibrationSpeed;
        this.flipMotorHysteresis = flipMotorHysteresis;
        this.twistMotorMotorCurrentLimit = twistMotorMotorCurrentLimit;
        this.twistMotorCalibrationMotorCurrentLimit = twistMotorCalibrationMotorCurrentLimit;
        this.twistMotorSpeed = twistMotorSpeed;
        this.twistMotorCalibrationSpeed = twistMotorCalibrationSpeed;
        this.twistMotorHysteresis = twistMotorHysteresis;
        this.twistMotorAngleOffset = twistMotorAngleOffset;
        this.targetId = targetId;
    }

    public int flipMotorMotorCurrentLimit() {
        return flipMotorMotorCurrentLimit;
    }

    public int flipMotorCalibrationMotorCurrentLimit() {
        return flipMotorCalibrationMotorCurrentLimit;
    }

    public int flipMotorSpeed() {
        return flipMotorSpeed;
    }

    public int flipMotorCalibrationSpeed() {
        return flipMotorCalibrationSpeed;
    }

    public int flipMotorHysteresis() {
        return flipMotorHysteresis;
    }

    public int twistMotorMotorCurrentLimit() {
        return twistMotorMotorCurrentLimit;
    }

    public int twistMotorCalibrationMotorCurrentLimit() {
        return twistMotorCalibrationMotorCurrentLimit;
    }

    public int twistMotorSpeed() {
        return twistMotorSpeed;
    }

    public int twistMotorCalibrationSpeed() {
        return twistMotorCalibrationSpeed;
    }

    public int twistMotorHysteresis() {
        return twistMotorHysteresis;
    }

    public int twistMotorAngleOffset() {
        return twistMotorAngleOffset;
    }

    public int targetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManufactureSettings that = (ManufactureSettings) o;
        return flipMotorMotorCurrentLimit == that.flipMotorMotorCurrentLimit &&
                flipMotorCalibrationMotorCurrentLimit == that.flipMotorCalibrationMotorCurrentLimit &&
                flipMotorSpeed == that.flipMotorSpeed &&
                flipMotorCalibrationSpeed == that.flipMotorCalibrationSpeed &&
                flipMotorHysteresis == that.flipMotorHysteresis &&
                twistMotorMotorCurrentLimit == that.twistMotorMotorCurrentLimit &&
                twistMotorCalibrationMotorCurrentLimit == that.twistMotorCalibrationMotorCurrentLimit &&
                twistMotorSpeed == that.twistMotorSpeed &&
                twistMotorCalibrationSpeed == that.twistMotorCalibrationSpeed &&
                twistMotorHysteresis == that.twistMotorHysteresis &&
                twistMotorAngleOffset == that.twistMotorAngleOffset &&
                targetId == that.targetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flipMotorMotorCurrentLimit,
                flipMotorCalibrationMotorCurrentLimit,
                flipMotorSpeed, flipMotorCalibrationSpeed,
                flipMotorHysteresis,
                twistMotorMotorCurrentLimit,
                twistMotorCalibrationMotorCurrentLimit,
                twistMotorSpeed,
                twistMotorCalibrationSpeed,
                twistMotorHysteresis,
                twistMotorAngleOffset,
                targetId);
    }
}
