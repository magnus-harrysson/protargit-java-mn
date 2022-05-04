package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Mechanical;
import com.harrys_it.ots.core.model.Motor;
import com.harrys_it.ots.core.model.Movement;
import com.harrys_it.ots.core.model.TargetMode;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import jakarta.inject.Singleton;

@Singleton
public class MechanicalService {
	private Mechanical activeConfig;
	private Motor flipMotor;
	private Motor twistMotor;
	private final Movement flipAutoMovement;
	private final Movement twistAutoMovement;

	public MechanicalService(SettingService settingService) {
		this.flipMotor = this.defaultFlipMotor(settingService);
		this.twistMotor = this.defaultTwistMotor(settingService);
		this.flipAutoMovement = this.defaultFlipMovement(settingService);
		this.twistAutoMovement = this.defaultTwistMovement(settingService);
		this.activeConfig = new Mechanical(null, null, TargetMode.HOME);
	}

	protected void setMotorAndMovementAndMode(TargetMode mode) {
		switch(mode){
			case STOP:
			case HOME:
				this.activeConfig = new Mechanical(null, null, mode);
				break;
			case FLIP_AUTO:
				this.activeConfig = new Mechanical(this.flipMotor, this.flipAutoMovement, mode);
				break;
			case TWIST_AUTO:
				this.activeConfig = new Mechanical(this.twistMotor, this.twistAutoMovement, mode);
				break;
			default:
				break;
		}
	}

	protected void setRealValues(McuEvent mcuEvent) {
		var cmd = mcuEvent.getCmd();
		var data = mcuEvent.getData();

		if (cmd.equals(McuCommand.FLIP_SPEED)) {
			flipMotor = new Motor.Builder(flipMotor)
					.medSpeed(data)
					.build();
		} else if (cmd.equals(McuCommand.FLIP_CURRENT_ANGLE)) {
			flipMotor = new Motor.Builder(flipMotor)
					.medAngle(data)
					.build();
		} else if (cmd.equals(McuCommand.TWIST_SPEED)) {
			twistMotor = new Motor.Builder(twistMotor)
					.medSpeed(data)
					.build();
		} else if (cmd.equals(McuCommand.TWIST_CURRENT_ANGLE)) {
			twistMotor = new Motor.Builder(twistMotor)
					.medAngle(data)
					.build();
		}
	}

	public Motor getFlipMotor() { return flipMotor; }
	public int getFlipMotorSpeed() { return flipMotor.getSpeed(); }
	public int getFlipMotorCurrentAngle() {	return flipMotor.getAngle(); }
	public Movement getFlipAutoMovement() { return flipAutoMovement; }

	public Motor getTwistMotor() { return twistMotor; }
	public int getTwistMotorSpeed() { return twistMotor.getSpeed();	}
	public int getTwistMotorCurrentAngle() { return twistMotor.getAngle();}
	public Movement getTwistAutoMovement() { return twistAutoMovement; }

	public Mechanical getActiveConfig() { return activeConfig; }

	private Motor defaultFlipMotor(SettingService settingService) {
		var settings = settingService.getManufactureSettings();
		return new Motor.Builder()
				.medMotorCurrentLimit(settings.flipMotorMotorCurrentLimit())
				.medCalibrationMotorCurrentLimit(settings.flipMotorCalibrationMotorCurrentLimit())
				.medSpeed(settings.flipMotorSpeed())
				.medCalibrationSpeed(settings.flipMotorCalibrationSpeed())
				.build();
	}

	private Motor defaultTwistMotor(SettingService settingService) {
		var settings = settingService.getManufactureSettings();
		return new Motor.Builder()
				.medMotorCurrentLimit(settings.twistMotorMotorCurrentLimit())
				.medCalibrationMotorCurrentLimit(settings.twistMotorCalibrationMotorCurrentLimit())
				.medCalibrationSpeed(settings.twistMotorCalibrationSpeed())
				.medSpeed(settings.twistMotorSpeed())
				.build();
	}

	private Movement defaultFlipMovement(SettingService settingService) {
		var settings = settingService.getUserSettings();
		return new Movement.Builder()
				.medHideTime(settings.flipMovementHideTime())
				.medShowTime(settings.flipMovementShowTime())
				.medRuns(settings.flipMovementRuns())
				.medHideAngle(settings.flipMovementHideAngle())
				.medShowAngle(settings.flipMovementShowAngle())
				.medSpeed(settings.flipMovementSpeed())
				.build();
	}

	private Movement defaultTwistMovement(SettingService settingService) {
		var settings = settingService.getUserSettings();
		return new Movement.Builder()
				.medHideTime(settings.twistMovementHideTime())
				.medShowTime(settings.twistMovementShowTime())
				.medRuns(settings.twistMovementRuns())
				.medHideAngle(settings.twistMovementHideAngle())
				.medShowAngle(settings.twistMovementShowAngle())
				.medSpeed(settings.twistMovementSpeed())
				.build();
	}
}
