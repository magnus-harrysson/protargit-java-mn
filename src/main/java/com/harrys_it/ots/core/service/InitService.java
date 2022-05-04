package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuDataLimit;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InitService {
	private final OsService osService;
	private final ButtonLedService buttonLedService;
	private final McuService mcuService;
	private final MechanicalService mechanicalService;

	private static final Logger log = LoggerFactory.getLogger(InitService.class);

	public InitService(OsService osService, ButtonLedService buttonLedService, McuService mcuService,
					   MechanicalService mechanicalService, @Value("${start.services:true}") boolean startService) {
		this.osService = osService;
		this.buttonLedService = buttonLedService;
		this.mcuService = mcuService;
		this.mechanicalService = mechanicalService;
		if(startService) {
			log.debug("{}", InitService.class.getName() + " Started");
			run();
		}
	}

	private void run() {
		buttonLedService.setBlinkInterval(500);
		playCautionAudio();
		mcuService.writeSerial(new McuEvent(McuCommand.TWIST_FACTORY_CALIBRATION, mechanicalService.getTwistMotor().getCalibrationSpeed()));
		mcuService.writeSerial(new McuEvent(McuCommand.FLIP_SPEED, mechanicalService.getFlipMotor().getSpeed()));
		mcuService.writeSerial(new McuEvent(McuCommand.TWIST_SPEED, mechanicalService.getTwistMotor().getSpeed()));
		setCurrentAngle();
		buttonLedService.setBlinkInterval(1);
	}

	private void setCurrentAngle() {
		int flipAngle = mcuService.writeSerial(new McuEvent(McuCommand.FLIP_CURRENT_ANGLE, McuDataLimit.ANY_DATA.getValue()));
		int twistAngle = mcuService.writeSerial(new McuEvent(McuCommand.TWIST_CURRENT_ANGLE, McuDataLimit.ANY_DATA.getValue()));
		mechanicalService.setRealValues(new McuEvent(McuCommand.FLIP_CURRENT_ANGLE, flipAngle));
		mechanicalService.setRealValues(new McuEvent(McuCommand.TWIST_CURRENT_ANGLE, twistAngle));
	}

	private void playCautionAudio() {
		try {
			osService.playAudioMp3("calibration.mp3");
			Thread.sleep(2000); // Wait some time before starting movement.
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
