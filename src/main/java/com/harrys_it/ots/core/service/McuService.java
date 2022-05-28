package com.harrys_it.ots.core.service;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.harrys_it.ots.core.model.SerialConnection;
import com.harrys_it.ots.core.model.SerialProtocols;
import com.harrys_it.ots.core.model.mcu.*;
import com.harrys_it.ots.core.utils.McuSerialDataConverter;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

@Singleton
public class McuService extends SerialConnection {
	private final BroadcasterService broadcasterService;
	private final MechanicalService mechanicalService;
	private final SettingService settingService;
	private volatile int dataOnSerial;
	private volatile boolean newDataOnSerial = false;
	private volatile boolean timeOut = false;
	private volatile long hitTimerStart = System.nanoTime();
	private static final int STX = 0x02;				/* START COMMAND */
	private static final int EXT = 0x03;				/* STOP COMMAND */
	private static final int DELIMITER = 0x3A;			/* USED FOR SEPARATE COMMAND AND DATA*/
	private static final int PACKET_SIZE_MAX = 16;		/* Response from mcu is max 16 bytes */
	private static final Logger log = LoggerFactory.getLogger(McuService.class);

	public McuService(BroadcasterService broadcasterService, MechanicalService mechanicalService,
                      SerialProtocolService serialProtocolService, SettingService settingService,
					  @Value("${hardware.services.enable}") boolean startService) {
		super(SerialProtocols.MCU,"/dev/ttyAMA0", 115200, 8, serialProtocolService);
		this.broadcasterService = broadcasterService;
		this.mechanicalService = mechanicalService;
		this.settingService = settingService;
		if(startService) {
			log.debug("Started");
			run();
		}
	}

	private void run() {
		new Thread(() -> getSerialPort().addDataListener(new SerialPortMessageListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public byte[] getMessageDelimiter() {
				return new byte[]{(byte) EXT};
			}

			@Override
			public boolean delimiterIndicatesEndOfMessage() {
				return true;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				byte[] response = event.getReceivedData();
				if (hasStartDelimiterAndStopBytes(response)) {
					var cmd = McuSerialDataConverter.commandToInt(response);
					var data = McuSerialDataConverter.dataToInt(response);
					if (McuUtils.errorOrHitOrStaticHitResponse(cmd)) {
						broadcastErrorHitAndStaticHit(cmd, data);
					} else {
						dataOnSerial = data;
						newDataOnSerial = true;
					}
				}
			}
		})).start();
	}

	private void broadcastErrorHitAndStaticHit(int cmd, int data) {
		int time = (int) ((System.nanoTime() - hitTimerStart) / 1000000);
		// If thw values is bigger than 32767 * 2. Set it to 32767 * 2 so it is within 2 bytes.
		if(time > Short.MAX_VALUE * 2){
			time = Short.MAX_VALUE * 2;
		}

		if(McuUtils.zoneHitResponse(cmd)) {
			// Since hit commands from MCU goes from 11-15. 11 is subtracted from response to get 0-4
			int resCmd = cmd - 11;
			broadcasterService.sendHit(new McuBroadcastMessage(resCmd, data, time));
		} else if(McuUtils.zoneStaticResponse(cmd)) {
			// Since static hit commands from MCU goes from 131-135. 131 is subtracted from response to get 0-4
			int resCmd = cmd - 131;
			broadcasterService.sendStaticHit(new McuBroadcastMessage(resCmd, data, time));
		} else {
			// Since other commands (ERROR) from MCU goes from 128-129. 128 is subtracted.
			int resCmd = cmd - 128;
			broadcasterService.sendOther(new McuBroadcastMessage(resCmd, data, time));
		}
	}

	public synchronized Integer writeSerial(McuEvent mcuEvent) {
    	if(isSerialPortOpen()) {

			byte[] res = checkAngleAndHysteresis(mcuEvent);
			if(res.length == 0) {
				return McuError.NOT_OVER_HYSTERESIS_LIMIT.getValue();
			}

	    	var timer = new Timer();
			timeOut = false;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					timeOut = true;
				}
			}, 4500);

	        newDataOnSerial = false;
			getSerialPort().writeBytes(res, res.length);

			if(isHitZoneEnableCommand(mcuEvent)) {
				hitTimerStart = System.nanoTime();
			}

			return waitForMcuResponse(mcuEvent, timer);

		} else {
    		return McuError.SERIAL_CLOSED.getValue();
    	}
    }

	private boolean isHitZoneEnableCommand(McuEvent mcuEvent) {
		return mcuEvent.getCmd().getValue() == McuCommand.HIT_ZONES.getValue() &&
				mcuEvent.getData() == McuDataLimit.HIT_ZONES_ENABLE.getValue();
	}

	private byte[] checkAngleAndHysteresis(McuEvent mcuEvent) {
		var isFlipAngleCommand = mcuEvent.getCmd().getValue() == McuCommand.FLIP_ANGLE.getValue();
		var isTwistAngleCommand = mcuEvent.getCmd().getValue() == McuCommand.TWIST_ANGLE.getValue();

		if(isFlipAngleCommand) {
			var shouldFlipMove = Math.abs(mechanicalService.getFlipMotorCurrentAngle() - mcuEvent.getData()) >= settingService.getManufactureSettings().flipMotorHysteresis();
			var convertedToBytes = McuSerialDataConverter.convertToASCII(mcuEvent.getCmd().getValue(), mcuEvent.getData(), PACKET_SIZE_MAX);
			return shouldFlipMove ? convertedToBytes : new byte[]{};
		}

		if(isTwistAngleCommand) {
			int twistAngleWithOffset = mcuEvent.getData() + settingService.getManufactureSettings().twistMotorAngleOffset();
			var shouldTwistMove = Math.abs(mechanicalService.getTwistMotorCurrentAngle() - twistAngleWithOffset) >= settingService.getManufactureSettings().twistMotorHysteresis();
			var convertedToBytes = McuSerialDataConverter.convertToASCII(mcuEvent.getCmd().getValue(), twistAngleWithOffset, PACKET_SIZE_MAX);
			return shouldTwistMove ? convertedToBytes : new byte[]{};
		}

		return McuSerialDataConverter.convertToASCII(mcuEvent.getCmd().getValue(), mcuEvent.getData(), PACKET_SIZE_MAX);

	}

	private Integer waitForMcuResponse(McuEvent mcuEvent, Timer timer) {
		while(true){

			threadSleep();

			if(timeOut) {
				resetTimer(timer);
				return McuError.TIMEOUT.getValue();
			}

			if(newDataOnSerial) {
				resetTimer(timer);
				if(dataOnSerial<0) {
					return McuError.SERIAL_ERROR.getValue();
				}
				setTargetValues(mcuEvent);
				return dataOnSerial;
			}
		}
	}

	private void setTargetValues(McuEvent mcuEvent) {
		mechanicalService.setRealValues(mcuEvent);

		if(mcuEvent.getCmd() == McuCommand.FLIP_ANGLE) {
			int resAngle = writeSerial(new McuEvent(McuCommand.FLIP_CURRENT_ANGLE, McuDataLimit.ANY_DATA.getValue()));
			mechanicalService.setRealValues(new McuEvent(McuCommand.FLIP_CURRENT_ANGLE, resAngle));
		} else if(mcuEvent.getCmd() == McuCommand.TWIST_ANGLE) {
			int resAngle = writeSerial(new McuEvent(McuCommand.TWIST_CURRENT_ANGLE, McuDataLimit.ANY_DATA.getValue()));
			mechanicalService.setRealValues(new McuEvent(McuCommand.TWIST_CURRENT_ANGLE, resAngle - settingService.getManufactureSettings().twistMotorAngleOffset()));
		}

		// Broadcast to subscriber: it´s time to update and send targetStatus.
		if(mcuEvent.getCmd().emitValueOnChange()) {
			broadcasterService.updateValues();
		}
	}

    private void resetTimer(Timer t) {
    	t.cancel();
    	t.purge();
    	timeOut = false;
    }

	private boolean hasStartDelimiterAndStopBytes(byte[] response) {
		var hasDelimiter = (
				response[2] == DELIMITER ||
				response[3] == DELIMITER ||
				response[4] == DELIMITER);

		return hasDelimiter && (response[0] == STX) && (response[response.length - 1] == EXT);
	}

	private void threadSleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			log.debug("InterruptedException in threadSleep");
			Thread.currentThread().interrupt();
		}
	}
}
