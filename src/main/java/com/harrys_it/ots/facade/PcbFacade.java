package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.model.TargetMode;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuErrorResponse;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.McuService;
import com.harrys_it.ots.core.service.ModeService;
import com.harrys_it.ots.core.service.ZonesService;
import jakarta.inject.Singleton;

@Singleton
public class PcbFacade {

    private final ModeService mode;
    private final McuService mcu;
    private final ZonesService zonesService;

    public PcbFacade(ModeService mode, McuService mcu, ZonesService zonesService) {
        this.mode = mode;
        this.mcu = mcu;
        this.zonesService = zonesService;
    }

    public void startMode(TargetMode targetMode) {
        mode.setMode(targetMode);
    }

    public Integer sendToMcu(McuEvent mcuEvent) {
        if(mcuEvent.getCmd().isProtect()) {
            return McuErrorResponse.MCU_EVENT_PROTECTED.getValue();
        } else if(mcuEvent.getCmd().getValue() == McuCommand.HIT_ZONES.getValue()) {
            return zonesService.setActive(mcuEvent) ? 1 : 0;
        } else {
            return mcu.writeSerial(mcuEvent);
        }
    }
}
