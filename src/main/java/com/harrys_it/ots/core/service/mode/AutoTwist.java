package com.harrys_it.ots.core.service.mode;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.core.service.McuService;
import com.harrys_it.ots.core.service.MechanicalService;
import com.harrys_it.ots.core.service.OsService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AutoTwist implements Runnable, PropertyChangeListener {
    private final MechanicalService motorAndMovementHandler;
    private final McuService mcuService;
    private final OsService osService;
    private boolean hitDetected = false;
    private volatile boolean exit = false;

    public AutoTwist(MechanicalService motorAndMovementHandler, McuService mcuService, OsService osService, BroadcasterService broadcasterService){
        this.motorAndMovementHandler = motorAndMovementHandler;
        this.mcuService = mcuService;
        this.osService = osService;
        broadcasterService.addPropertyChangeListener(this);
    }

    @Override
    public void run() {
        try {
            mcuService.writeSerial(new McuEvent(McuCommand.FLIP_ANGLE, motorAndMovementHandler.getFlipAutoMovement().getHideAngle()));
            mcuService.writeSerial(new McuEvent(McuCommand.TWIST_ANGLE, motorAndMovementHandler.getTwistAutoMovement().getShowAngle()));
            mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_ENABLE.getValue()));
            osService.playAudioMp3("twist.mp3");
        } catch (InterruptedException e) {
            exit = true;
            Thread.currentThread().interrupt();
        }

        while(!exit) {
            threadSleep(20);
            if(hitDetected) {
                mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_DISABLE.getValue()));
                mcuService.writeSerial(new McuEvent(McuCommand.TWIST_ANGLE, motorAndMovementHandler.getTwistAutoMovement().getHideAngle()));

                threadSleep(motorAndMovementHandler.getTwistAutoMovement().getHideTime());

                mcuService.writeSerial(new McuEvent(McuCommand.TWIST_ANGLE, motorAndMovementHandler.getTwistAutoMovement().getShowAngle()));
                mcuService.writeSerial(new McuEvent(McuCommand.HIT_ZONES, McuDataLimit.HIT_ZONES_ENABLE.getValue()));
                this.hitDetected = false;
            }
        }
    }

    private void threadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            exit = true;
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(BroadcastEvent.HIT.getValue())) {
            this.hitDetected = true;
        }
    }
}
