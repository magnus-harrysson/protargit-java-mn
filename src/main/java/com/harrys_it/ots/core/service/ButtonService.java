package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import com.harrys_it.ots.core.model.TargetMode;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ButtonService {
	private final ModeService modeService;
	private final OsService osService;
	private final SettingService settingService;

	private volatile TargetMode state = TargetMode.STOP;
	private volatile boolean changeState = false;

	private static final Logger log = LoggerFactory.getLogger(ButtonService.class);

	public ButtonService(ModeService modeService, OsService osService, SettingService settingService,
						 @Value("${start.services:true}") boolean startService){
		this.modeService = modeService;
		this.osService = osService;
		this.settingService = settingService;
		if(startService) {
			log.debug("{}", ButtonService.class.getName() + " Started");
			run();
		}
	}

	private void run() {
		new Thread(() -> {
			while (true) {
				threadSleep();
				buttonState();
				if (changeState) {
					changeState = false;
					switch (state) {
						case HOME -> modeService.setMode(TargetMode.HOME);
						case FLIP_AUTO -> modeService.setMode(TargetMode.FLIP_AUTO);
						case TWIST_AUTO -> modeService.setMode(TargetMode.TWIST_AUTO);
						default -> { /* do nothing*/ }
					}
				}
			}
		}).start();
	}

	private void buttonState() {
		try {
			var startTime = System.currentTimeMillis();
			var buttonReleased = false;
			long timeElapsed = 0;
			while (osService.command(Os.GPIO5_BUTTON_READ).contains("GPIO 5: level=0")) {
				long endTime = System.currentTimeMillis();
				timeElapsed = endTime - startTime;
				buttonReleased = true;
			}
			if (buttonReleased) {
				changeState(timeElapsed);
			}
		} catch (InterruptedException e) {
			log.debug("buttonState() execute:", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Change state or load default settings
	 * depending on how long the button is pressed
	 * @param timeElapsed Time pressed in ms.
	 */
	private synchronized void changeState(long timeElapsed) {
		if (timeElapsed > 50 && timeElapsed < 5000) {
			nextState();
		} else if (timeElapsed > 10000) {
			log.debug("Loading default user- and manufacture settings");
			settingService.loadDefaultSettings();
		}
	}

	private void nextState() {
		switch (state) {
			case STOP:
			case HOME:
				this.state = TargetMode.FLIP_AUTO;
				break;
			case FLIP_AUTO:
				this.state = TargetMode.TWIST_AUTO;
				break;
			case TWIST_AUTO:
				this.state = TargetMode.HOME;
				break;
		}
		changeState = true;
	}

	private void threadSleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			log.debug("Main InterruptedException:", e);
			Thread.currentThread().interrupt();
		}
	}
}
