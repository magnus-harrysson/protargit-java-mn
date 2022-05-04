package com.harrys_it.ots.core.model.mcu;

import java.util.Objects;

public class McuBroadcastMessage {
    private final Integer command;
    private final Integer data;
    private final Integer time;

    public McuBroadcastMessage(int command, int data) {
        this.command = command;
        this.data = data;
        this.time = 0;
    }
    public McuBroadcastMessage(int command, int data, int time) {
        this.command = command;
        this.data = data;
        this.time = time;
    }

    public int getCommand() {
        return command;
    }

    public int getData() {
        return data;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McuBroadcastMessage that = (McuBroadcastMessage) o;
        return command.equals(that.command) && data.equals(that.data) && time.equals(that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, data, time);
    }

    @Override
    public String toString() {
        return "McuBroadcastMessage{" +
                "command=" + command +
                ", data=" + data +
                ", time=" + time +
                '}';
    }
}
