package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import jakarta.inject.Singleton;

@Singleton
public class ZonesService {
    private final McuService mcuService;
    private boolean zonesActive = false;
    public ZonesService(McuService mcuService) {
        this.mcuService = mcuService;
    }

    public boolean isZonesActive() {
        return zonesActive;
    }

    public boolean setActive(McuEvent mcuEvent) {
        if (mcuEvent.getData() == McuDataLimit.HIT_ZONES_ENABLE.getValue()) {
            mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_ENABLE.getValue()));
            this.zonesActive = true;
        } else {
            mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_DISABLE.getValue()));
            this.zonesActive = false;
        }
        return zonesActive;
    }
}
