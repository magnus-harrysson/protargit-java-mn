package com.harrys_it.ots.core.service;

import jakarta.inject.Singleton;

/**
 * Set and get active Serialport:s
 */
@Singleton
public class SerialProtocolService {
    private boolean gpsEnable = false;
    private boolean mcuEnable = false;
    private boolean usbTtyEnable = false;
    private boolean bluetoothEnable = false;

    public SerialProtocolService() { /* Used by test */ }

    public boolean isGpsEnable() {
        return gpsEnable;
    }

    public void setGpsEnable(boolean gpsEnable) {
        this.gpsEnable = gpsEnable;
    }

    public boolean isMcuEnable() {
        return mcuEnable;
    }

    public void setMcuEnable(boolean mcuEnable) {
        this.mcuEnable = mcuEnable;
    }

    public boolean isUsbTtyEnable() {
        return usbTtyEnable;
    }

    public void setUsbTtyEnable(boolean usbTtyEnable) {
        this.usbTtyEnable = usbTtyEnable;
    }

    public boolean isBluetoothEnable() {
        return bluetoothEnable;
    }

    public void setBluetoothEnable(boolean bluetoothEnable) {
        this.bluetoothEnable = bluetoothEnable;
    }
}
