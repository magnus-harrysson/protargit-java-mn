package com.harrys_it.ots.core.model;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.harrys_it.ots.core.service.SerialProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SerialConnection {
    private SerialPort serialPort;
    private boolean isSerialPortOpen;
    private final SerialProtocolService serialProtocolService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialConnection.class);

    protected SerialConnection(SerialProtocols protocol, String port, int baudRate, int numberBits, SerialProtocolService serialProtocolService) {
        this.serialProtocolService = serialProtocolService;
        try {
            isSerialPortOpen = setup(port, baudRate, numberBits);
        } catch (SerialPortInvalidPortException e) {
            // Set a default port, so it still can be used without physical hardware
            isSerialPortOpen = setup("/dev/tty", baudRate, numberBits);
        }
        setSoftwareEnable(protocol);
    }

    private boolean setup(String port, int baudRate, int numberBits) {
        serialPort = SerialPort.getCommPort(port);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(numberBits);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        return serialPort.openPort();
    }

    private void setSoftwareEnable(SerialProtocols protocol){
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} serialport open: {}", protocol.name(), isSerialPortOpen);
        }
        if(isSerialPortOpen) {
            switch (protocol) {
                case BLUETOOTH -> serialProtocolService.setBluetoothEnable(true);
                case SERIAL_USB -> serialProtocolService.setUsbTtyEnable(true);
                case MCU -> serialProtocolService.setMcuEnable(true);
                case GPS -> serialProtocolService.setGpsEnable(true);
                default -> { /* do nothing */ }
            }
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public boolean isSerialPortOpen() {
        return isSerialPortOpen;
    }
}
