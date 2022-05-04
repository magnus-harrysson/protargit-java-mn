package com.harrys_it.ots.core.model.settings;

import java.util.Objects;

public class UserSettings {

    private final int flipMovementShowTime;
    private final int flipMovementHideTime;
    private final int flipMovementRuns;
    private final int flipMovementHideAngle;
    private final int flipMovementShowAngle;
    private final int flipMovementSpeed;

    private final int twistMovementShowTime;
    private final int twistMovementHideTime;
    private final int twistMovementRuns;
    private final int twistMovementHideAngle;
    private final int twistMovementShowAngle;
    private final int twistMovementSpeed;

    @SuppressWarnings("squid:S107")
    public UserSettings(int flipMovementShowTime,
                        int flipMovementHideTime,
                        int flipMovementRuns,
                        int flipMovementHideAngle,
                        int flipMovementShowAngle,
                        int flipMovementSpeed,
                        int twistMovementShowTime,
                        int twistMovementHideTime,
                        int twistMovementRuns,
                        int twistMovementHideAngle,
                        int twistMovementShowAngle,
                        int twistMovementSpeed) {

        this.flipMovementShowTime = flipMovementShowTime;
        this.flipMovementHideTime = flipMovementHideTime;
        this.flipMovementRuns = flipMovementRuns;
        this.flipMovementHideAngle = flipMovementHideAngle;
        this.flipMovementShowAngle = flipMovementShowAngle;
        this.flipMovementSpeed = flipMovementSpeed;

        this.twistMovementShowTime = twistMovementShowTime;
        this.twistMovementHideTime = twistMovementHideTime;
        this.twistMovementRuns = twistMovementRuns;
        this.twistMovementHideAngle = twistMovementHideAngle;
        this.twistMovementShowAngle = twistMovementShowAngle;
        this.twistMovementSpeed = twistMovementSpeed;
    }

    public int flipMovementShowTime() {
        return flipMovementShowTime;
    }

    public int flipMovementHideTime() {
        return flipMovementHideTime;
    }

    public int flipMovementRuns() {
        return flipMovementRuns;
    }

    public int flipMovementHideAngle() {
        return flipMovementHideAngle;
    }

    public int flipMovementShowAngle() {
        return flipMovementShowAngle;
    }

    public int flipMovementSpeed() {
        return flipMovementSpeed;
    }

    public int twistMovementShowTime() {
        return twistMovementShowTime;
    }

    public int twistMovementHideTime() {
        return twistMovementHideTime;
    }

    public int twistMovementRuns() {
        return twistMovementRuns;
    }

    public int twistMovementHideAngle() {
        return twistMovementHideAngle;
    }

    public int twistMovementShowAngle() {
        return twistMovementShowAngle;
    }

    public int twistMovementSpeed() {
        return twistMovementSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return flipMovementShowTime == that.flipMovementShowTime && flipMovementHideTime == that.flipMovementHideTime && flipMovementRuns == that.flipMovementRuns && flipMovementHideAngle == that.flipMovementHideAngle && flipMovementShowAngle == that.flipMovementShowAngle && flipMovementSpeed == that.flipMovementSpeed && twistMovementShowTime == that.twistMovementShowTime && twistMovementHideTime == that.twistMovementHideTime && twistMovementRuns == that.twistMovementRuns && twistMovementHideAngle == that.twistMovementHideAngle && twistMovementShowAngle == that.twistMovementShowAngle && twistMovementSpeed == that.twistMovementSpeed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flipMovementShowTime, flipMovementHideTime, flipMovementRuns, flipMovementHideAngle, flipMovementShowAngle, flipMovementSpeed, twistMovementShowTime, twistMovementHideTime, twistMovementRuns, twistMovementHideAngle, twistMovementShowAngle, twistMovementSpeed);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "flipMovementShowTime=" + flipMovementShowTime +
                ", flipMovementHideTime=" + flipMovementHideTime +
                ", flipMovementRuns=" + flipMovementRuns +
                ", flipMovementHideAngle=" + flipMovementHideAngle +
                ", flipMovementShowAngle=" + flipMovementShowAngle +
                ", flipMovementSpeed=" + flipMovementSpeed +
                ", twistMovementShowTime=" + twistMovementShowTime +
                ", twistMovementHideTime=" + twistMovementHideTime +
                ", twistMovementRuns=" + twistMovementRuns +
                ", twistMovementHideAngle=" + twistMovementHideAngle +
                ", twistMovementShowAngle=" + twistMovementShowAngle +
                ", twistMovementSpeed=" + twistMovementSpeed +
                '}';
    }
}
