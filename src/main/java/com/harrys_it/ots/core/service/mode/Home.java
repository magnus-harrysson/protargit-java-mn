package com.harrys_it.ots.core.service.mode;

import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.McuService;
import com.harrys_it.ots.core.service.MechanicalService;
import com.harrys_it.ots.core.service.OsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Home implements Runnable {

    private final MechanicalService mechanicalService;
    private final McuService mcuService;
    private final OsService osService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Home.class);

    public Home(MechanicalService mechanicalService, McuService mcuService, OsService osService) {
        this.mechanicalService = mechanicalService;
        this.mcuService = mcuService;
        this.osService = osService;
    }

    @Override
    public void run() {
        try {
            osService.playAudioMp3("homing.mp3");
        } catch (InterruptedException e) {
            LOGGER.debug("HOME: play audio:", e);
            Thread.currentThread().interrupt();
        }
        mcuService.writeSerial(new McuEvent(McuCommand.FLIP_ANGLE, mechanicalService.getFlipAutoMovement().getHideAngle()));
        mcuService.writeSerial(new McuEvent(McuCommand.TWIST_ANGLE, mechanicalService.getTwistAutoMovement().getHideAngle()));
        mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_DISABLE.getValue()));
    }
}
