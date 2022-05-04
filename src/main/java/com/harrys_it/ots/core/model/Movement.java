package com.harrys_it.ots.core.model;

import java.util.Objects;

public class Movement {

    private final int showTime;
    private final int hideTime;
    private final int runs;
    private final int hideAngle;
    private final int showAngle;
    private final int speed;

    private Movement(Builder builder){
        this.showTime = builder.showTime;
        this.hideTime = builder.hideTime;
        this.runs = builder.runs;
        this.hideAngle = builder.hideAngle;
        this.showAngle = builder.showAngle;
        this.speed = builder.speed;
    }

    public int getShowTime() {
        return showTime;
    }

    public int getHideTime() {
        return hideTime;
    }

    public int getRuns() {
        return runs;
    }

    public int getHideAngle() {
        return hideAngle;
    }

    public int getShowAngle() {
        return showAngle;
    }

    public int getSpeed() {
        return speed;
    }

    public static class Builder {
        private int showTime;
        private int hideTime;
        private int runs;
        private int hideAngle;
        private int showAngle;
        private int speed;

        public Builder() { }

        public Builder(Movement movement) {
            this.showTime = movement.showTime;
            this.hideTime = movement.hideTime;
            this.runs = movement.runs;
            this.hideAngle = movement.hideAngle;
            this.showAngle = movement.showAngle;
            this.speed = movement.speed;
        }

        public Builder medShowTime(int showTime) {
            this.showTime = showTime;
            return this;
        }

        public Builder medHideTime(int hideTime) {
            this.hideTime = hideTime;
            return this;
        }

        public Builder medRuns(int runs) {
            this.runs = runs;
            return this;
        }

        public Builder medHideAngle(int hideAngle) {
            this.hideAngle = hideAngle;
            return this;
        }

        public Builder medShowAngle(int showAngle) {
            this.showAngle = showAngle;
            return this;
        }

        public Builder medSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public Movement build() {
            return new Movement(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return showTime == movement.showTime &&
                hideTime == movement.hideTime &&
                runs == movement.runs &&
                hideAngle == movement.hideAngle &&
                showAngle == movement.showAngle &&
                speed == movement.speed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showTime, hideTime, runs, hideAngle, showAngle, speed);
    }

    @Override
    public String toString() {
        return "Movement{" +
                "showTime=" + showTime +
                ", hideTime=" + hideTime +
                ", runs=" + runs +
                ", hideAngle=" + hideAngle +
                ", showAngle=" + showAngle +
                ", speed=" + speed +
                '}';
    }
}
