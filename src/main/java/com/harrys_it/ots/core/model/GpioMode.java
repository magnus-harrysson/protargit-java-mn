package com.harrys_it.ots.core.model;

public enum GpioMode {
    ON_OFF((byte) 0x00),
    ON_TIMER((byte) 0x01),
    OFF_TIMER((byte) 0x02);

    private final byte value;
    GpioMode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GpioMode{" +
                "value=" + value +
                '}';
    }
}
