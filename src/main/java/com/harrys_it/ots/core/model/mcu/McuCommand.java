package com.harrys_it.ots.core.model.mcu;

public enum McuCommand {
    PING(1, false, true, "Ping to MCU"),

    FLIP_MOTOR_CURRENT_LIMIT(2, false, false, "Set ampere limit for flip motor, error event 128 will be emitted if overpower"),
    FLIP_FACTORY_CALIBRATION(3, true, false, "Set home angle (0) for flip motor"),
    FLIP_SPEED(5, false, true, "Set flip speed"),
    FLIP_ANGLE(6, false, true, "Set flip angle"),
    FLIP_CURRENT_ANGLE(7, false, false, "Get current flip angle"),

    STATIC_HIT_ZONES(10, false, false, "If any hit zone is static, this event will be emitted"),
    HIT_ZONE_1(11, false, false, "Hit in zone 1, this event will be emitted"),
    HIT_ZONE_2(12, false, false, "Hit in zone 2, this event will be emitted"),
    HIT_ZONE_3(13, false, false, "Hit in zone 3, this event will be emitted"),
    HIT_ZONE_4(14, false, false, "Hit in zone 4, this event will be emitted"),
    HIT_ZONE_5(15, false, false, "Hit in zone 5, this event will be emitted"),
    HIT_ZONES(16, false, true, "Enable/Disable hit zones"),

    TWIST_MOTOR_CURRENT_LIMIT(18, false, false, "Set ampere limit for twist motor, error event 128 will be emitted if overpower"),
    TWIST_FACTORY_CALIBRATION(19, true, false, "Set home angle (0) for twist motor"),
    TWIST_SPEED(21, false, true, "Set twist speed"),
    TWIST_ANGLE(22, false, true, "Set twist angle"),
    TWIST_CURRENT_ANGLE(23, false, false, "Get current twist angle"),

    BATTERY_CAPACITY(46, false, true, "Get current battery capacity"),
    BATTERY_VOLTAGE(47, false, true, "Get current battery voltage"),

    MAJOR(125, false, false, "Get MCU firmware version, major"),
    MINOR(126, false, false, "Get MCU firmware version, minor"),
    BUILD(127, false, false, "Get MCU firmware version, build"),

    EXCESS_CURRENT_DURING_MOVEMENT(128, false, false, "Will be emitted when ampere limit is exceeded"),
    MOTOR_TIMEOUT(129, false, false, "Will be emitted when motor timeout"),
    STATIC_ZONE_1(131, false, false, "Will be emitted when hit zone 1 is grounded for more than 40 ms"),
    STATIC_ZONE_2(132, false, false, "Will be emitted when hit zone 2 is grounded for more than 40 ms"),
    STATIC_ZONE_3(133, false, false, "Will be emitted when hit zone 3 is grounded for more than 40 ms"),
    STATIC_ZONE_4(134, false, false, "Will be emitted when hit zone 4 is grounded for more than 40 ms"),
    STATIC_ZONE_5(135, false, false, "Will be emitted when hit zone 5 is grounded for more than 40 ms"),

    TWOCOM_BUS_START(160, false, false, "(STX) Send START string over TWOCOM"),
    TWOCOM_BUS_END(192,false, false, "(EXT) Send END string over TWOCOM"),
    PROCESSOR_RESET(255, true, false, "Reset the processor");

    private final int value;
    private final boolean protect;
    private final boolean emitValueOnChange;
    private final String description;
    
    McuCommand(int value, boolean protect, boolean emitValueOnChange, String description) {
        this.value = value;
        this.protect = protect;
        this.emitValueOnChange = emitValueOnChange;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public boolean isProtect() {
        return protect;
    }

    public boolean emitValueOnChange() {
        return emitValueOnChange;
    }

    public String getDescription() {
        return description;
    }

    public static McuCommand fromInt(int mcuCommand) {
        for (McuCommand m : McuCommand.values()) {
            if (m.value == mcuCommand) {
                return m;
            }
        }
        throw new IllegalArgumentException("No constant with value " + mcuCommand + " found");
    }

    @Override
    public String toString() {
        return "McuCommand{" +
                "value=" + value +
                ", protect=" + protect +
                ", emitValueOnChange=" + emitValueOnChange +
                ", description='" + description + '\'' +
                '}';
    }
}
