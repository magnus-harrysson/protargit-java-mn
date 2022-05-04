package com.harrys_it.ots.core.model.mcu;

public enum McuDataLimit {
    ANY_DATA(0),

    PING(0),

    FLIP_MOTOR_CURRENT_LIMITATION_MIN(800),
    FLIP_MOTOR_CURRENT_LIMITATION_MAX(4000),
    FLIP_FACTORY_CALIBRATION_SPEED_MIN(20),  	// Speed during calibration command
    FLIP_FACTORY_CALIBRATION_SPEED_MAX(99),  	// Speed during calibration command
    FLIP_SPEED_MIN(20),
    FLIP_SPEED_MAX(99),
    FLIP_ANGLE_MIN(0),
    FLIP_ANGLE_MAX(150),

    HIT_ZONES_DISABLE(0),
    HIT_ZONES_ENABLE(1),

    TWIST_MOTOR_CURRENT_LIMITATION_MIN(800),
    TWIST_MOTOR_CURRENT_LIMITATION_MAX(4000),
    TWIST_FACTORY_CALIBRATION_SPEED_MIN(20),  	// Speed during calibration command
    TWIST_FACTORY_CALIBRATION_SPEED_MAX(99),  	// Speed during calibration command
    TWIST_SPEED_MIN(20),
    TWIST_SPEED_MAX(99),
    TWIST_ANGLE_MIN(0),
    TWIST_ANGLE_MAX(270),

    PROCESSOR_RESET(1);

    private final int value;

    McuDataLimit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "McuDataLimit{" +
                "value=" + value +
                '}';
    }
}
