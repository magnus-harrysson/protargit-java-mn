package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.TargetMode;
import com.harrys_it.ots.core.service.mode.AutoFlip;
import com.harrys_it.ots.core.service.mode.AutoTwist;
import com.harrys_it.ots.core.service.mode.Home;
import com.harrys_it.ots.core.service.mode.Stop;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ModeService {
    private final OsService osService;
    private final McuService mcuService;
    private final MechanicalService mechanicalService;
    private final BroadcasterService broadcasterService;
    private final ButtonLedService buttonLedService;
    private Thread modeThread = null;
    private volatile boolean changeState = false;
    private static final Logger log = LoggerFactory.getLogger(ModeService.class);

    public ModeService(OsService osService,
                       McuService mcuService,
                       MechanicalService mechanicalService,
                       BroadcasterService broadcasterService,
                       ButtonLedService buttonLedService,
                       @Value("${hardware.services.enable}") boolean startService){
        this.osService = osService;
        this.mcuService = mcuService;
        this.mechanicalService = mechanicalService;
        this.broadcasterService = broadcasterService;
        this.buttonLedService = buttonLedService;
        if(startService) {
            log.debug("Started");
            run();
        }
    }


    private void run() {
        new Thread(() -> {
            while (true) {
                threadSleep();
                switchMode();
            }
        }).start();
    }

    private void switchMode() {
        if (changeState) {
            changeState = false;
            buttonLedService.setBlinkInterval(1);
            switch (mechanicalService.getActiveConfig().targetMode()) {
                case STOP:
                    modeThread = new Thread(new Stop(mcuService));
                    break;
                case HOME:
                    modeThread = new Thread(new Home(mechanicalService, mcuService, osService));
                    break;
                case FLIP_AUTO:
                    modeThread = new Thread(new AutoFlip(mechanicalService, mcuService, osService, broadcasterService));
                    break;
                case TWIST_AUTO:
                    modeThread = new Thread(new AutoTwist(mechanicalService, mcuService, osService, broadcasterService));
                    break;
                default:
                    break;
            }
            modeThread.start();
        }
    }

    public void setMode(TargetMode mode) {
        stopCurrentMode();
        mechanicalService.setMotorAndMovementAndMode(mode);
        this.changeState = true;
    }

    private void stopCurrentMode() {
        if(modeThread!=null){
            modeThread.interrupt();
            try {
                modeThread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void threadSleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.debug("Main thread ModeHandling: ",e);
            Thread.currentThread().interrupt();
        }
    }
}
