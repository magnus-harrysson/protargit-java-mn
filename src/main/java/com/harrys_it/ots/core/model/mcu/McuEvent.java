package com.harrys_it.ots.core.model.mcu;

import java.util.Objects;

public class McuEvent {
    private final McuCommand cmd;
    private final int data;

    public McuEvent(McuCommand mcuCommand, int mcuDataLimits) {
        if(!McuUtils.validateDataAndCmd(mcuCommand, mcuDataLimits)){
            throw new McuException("Can not create McuEvent with cmd:" + mcuCommand + " data:" + mcuDataLimits);
        }
        this.cmd = mcuCommand;
        this.data = mcuDataLimits;
    }

    public McuCommand getCmd() {
        return cmd;
    }

    public int getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McuEvent mcuEvent = (McuEvent) o;
        return data == mcuEvent.data && cmd == mcuEvent.cmd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, data);
    }

    @Override
    public String toString() {
        return "McuEvent{" +
                "cmd=" + cmd +
                ", data=" + data +
                '}';
    }
}
