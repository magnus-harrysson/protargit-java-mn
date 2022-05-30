package com.harrys_it.ots.core.model.mcu;

public enum McuError {
    SERIAL_CLOSED(-100),
    TIMEOUT(-101),
    SERIAL_ERROR(-102),
    NOT_VALID(-103),
    NOT_OVER_HYSTERESIS_LIMIT(-104),
    MCU_COMMAND_PROTECTED(-105);

    private final Integer value;

    McuError(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "McuErrorResponse{" +
                "value=" + value +
                '}';
    }
}
