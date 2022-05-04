package com.harrys_it.ots.core.model.mcu;

public enum McuErrorResponse {
    SERIAL_CLOSED(-100),
    TIMEOUT(-101),
    SERIAL_ERROR(-102),
    NOT_VALID(-103),
    NOT_OVER_HYSTERESIS_LIMIT(-104),
    MCU_EVENT_PROTECTED(-105);

    private final Integer value;

    McuErrorResponse(Integer value) {
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
