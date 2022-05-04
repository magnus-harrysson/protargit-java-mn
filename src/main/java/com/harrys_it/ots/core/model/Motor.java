package com.harrys_it.ots.core.model;

import java.util.Objects;

public class Motor {
	private final int calibrationMotorCurrentLimit;
	private final int motorCurrentLimit;
	private final int calibrationSpeed;
	private final int speed;
	private final int angle;

	private Motor(Builder builder) {
		this.calibrationMotorCurrentLimit = builder.calibrationMotorCurrentLimit;
		this.motorCurrentLimit = builder.motorCurrentLimit;
		this.calibrationSpeed = builder.calibrationSpeed;
		this.speed = builder.speed;
		this.angle = builder.angle;
	}

	public int getCalibrationSpeed() {
		return calibrationSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public int getAngle() {
		return angle;
	}

	public static class Builder {
		private int calibrationMotorCurrentLimit;
		private int motorCurrentLimit;
		private int calibrationSpeed;
		private int speed;
		private int angle;

		public Builder() { }

		public Builder(Motor motor) {
			this.calibrationMotorCurrentLimit = motor.calibrationMotorCurrentLimit;
			this.motorCurrentLimit = motor.motorCurrentLimit;
			this.calibrationSpeed = motor.calibrationSpeed;
			this.speed = motor.speed;
			this.angle = motor.angle;
		}

		public Builder medCalibrationMotorCurrentLimit(int calibrationMotorCurrentLimit) {
			this.calibrationMotorCurrentLimit = calibrationMotorCurrentLimit;
			return this;
		}

		public Builder medMotorCurrentLimit(int motorCurrentLimit) {
			this.motorCurrentLimit = motorCurrentLimit;
			return this;
		}

		public Builder medCalibrationSpeed(int calibrationSpeed) {
			this.calibrationSpeed = calibrationSpeed;
			return this;
		}

		public Builder medSpeed(int speed) {
			this.speed = speed;
			return this;
		}

		public Builder medAngle(int angle) {
			this.angle = angle;
			return this;
		}

		public Motor build() {
			return new Motor(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Motor motor = (Motor) o;
		return calibrationMotorCurrentLimit == motor.calibrationMotorCurrentLimit &&
				motorCurrentLimit == motor.motorCurrentLimit &&
				calibrationSpeed == motor.calibrationSpeed &&
				speed == motor.speed &&
				angle == motor.angle;
	}

	@Override
	public int hashCode() {
		return Objects.hash(calibrationMotorCurrentLimit, motorCurrentLimit, calibrationSpeed, speed, angle);
	}

	@Override
	public String toString() {
		return "Motor{" +
				"calibrationMotorCurrentLimit=" + calibrationMotorCurrentLimit +
				", motorCurrentLimit=" + motorCurrentLimit +
				", calibrationSpeed=" + calibrationSpeed +
				", speed=" + speed +
				", angle=" + angle +
				'}';
	}
}
