package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
class ZonesServiceTest {

    private ZonesService zonesService;

    @Mock
    private McuService mcuService;

    @BeforeEach
    void setup() {
        zonesService = new ZonesService(mcuService);
    }

    @Test
    void shouldSetZonesToActive() {
        McuEvent mcuEvent = new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_ENABLE.getValue());
        Mockito.lenient().doReturn(1).when(mcuService).writeSerial(mcuEvent);

        var call = zonesService.setActive(mcuEvent);
        assertTrue(call);
    }

    @Test
    void shouldSetZonesToInactive() {
        McuEvent mcuEvent = new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_DISABLE.getValue());
        when(mcuService.writeSerial(mcuEvent)).thenReturn(1);

        var call = zonesService.setActive(mcuEvent);
        assertFalse(call);
    }
}
