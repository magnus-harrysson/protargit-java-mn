package com.harrys_it.ots.core.service.mode;

import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.McuService;

public class Stop implements Runnable{
    private final McuService mcuService;

    public Stop(McuService mcuService) {
        this.mcuService = mcuService;
    }

    @Override
    public void run() {
        mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_DISABLE.getValue()));
    }
}
