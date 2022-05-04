package com.harrys_it.ots.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SerialProtocolServiceTest {

    private SerialProtocolService serialProtocolService;

    @BeforeEach
    void setup() {
        serialProtocolService = new SerialProtocolService();
    }

    @Test
    void shouldSetAndGetValues() {
        serialProtocolService.setGpsEnable(true);
        serialProtocolService.setMcuEnable(true);
        serialProtocolService.setUsbTtyEnable(true);
        serialProtocolService.setBluetoothEnable(true);

        assertTrue(serialProtocolService.isGpsEnable());
        assertTrue(serialProtocolService.isMcuEnable());
        assertTrue(serialProtocolService.isUsbTtyEnable());
        assertTrue(serialProtocolService.isBluetoothEnable());
    }
}
