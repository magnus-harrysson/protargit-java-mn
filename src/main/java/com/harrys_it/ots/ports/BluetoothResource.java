package com.harrys_it.ots.ports;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.SerialConnection;
import com.harrys_it.ots.core.model.SerialProtocols;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.core.service.SerialProtocolService;
import com.harrys_it.ots.ports.utils.BluetoothAndWebsocketProtocol;
import com.harrys_it.ots.ports.utils.ProtocolContract;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *   [HEADER][LEN][BODY] where [BODY] -> [ID][DATA]
 *      [HEADER] = CMD = t.ex:0xF0 send data. 0xF2 receive data. 0xF1 data GROUP Tx
 *      [LEN] = Message length
 *      BODY -> [ID][DATA]
 *          [ID] = Reviver address (0-65535) 2 bytes
 *          [DATA] = Data to be sent (will be sent in packages on 11 bytes and up to 253 bytes)
 */
@Singleton
public class BluetoothResource extends SerialConnection implements PropertyChangeListener {

    private final BroadcasterService broadcasterService;
    private final BluetoothAndWebsocketProtocol protocol;

    private static final String COM_PORT = "/dev/ttyS0";

    private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothResource.class);

    public BluetoothResource(BroadcasterService broadcasterService,
                             SerialProtocolService serialProtocolService,
                             BluetoothAndWebsocketProtocol protocol) {
        super(SerialProtocols.BLUETOOTH, COM_PORT, 115200, 8, serialProtocolService);
        this.broadcasterService = broadcasterService;
        this.protocol = protocol;
    }

    @Value("${start.serial.bluetooth}")
    private void run(boolean startSerialBluetooth) {
        LOGGER.debug("is up:{}, port:{}", startSerialBluetooth, COM_PORT);
        if(startSerialBluetooth) {
            broadcasterService.addPropertyChangeListener(this); // Listen to hits
            startListeningOnSerial();
            LOGGER.debug("Sending target information to proxy over bluetooth");
            this.propertyChange(new PropertyChangeEvent(BroadcasterService.class, BroadcastEvent.UPDATE_TARGET_STATUS.getValue(), "old-update", "new-update"));
        }
    }

    /** SerialPortEvent event
     * bytes[0-3] is serial-rx (0xF2), length, bt-address1, bt-address2.
     * byte [4] is inCommand
     * byte [5-x] data. (x = packet length - 1).
     * byte [last byte] is the slave ID
     * event from serial starting with 0xF2 0x0D
     */
    private void startListeningOnSerial() {
        new Thread(() -> getSerialPort().addDataListener(new SerialPortMessageListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
            @Override
            public byte[] getMessageDelimiter() { return new byte[] { ProtocolContract.SERIAL.RECEIVE.getValue(), ProtocolContract.SERIAL.DATA_LENGTH.getValue()}; }
            @Override
            public boolean delimiterIndicatesEndOfMessage() { return false; }
            @Override
            public void serialEvent(SerialPortEvent event) {
                new Thread(() -> {
                    byte[] in = event.getReceivedData();
                    var response = protocol.handleDataFromMaster(in, this.getClass().getName());
                    if(response.length > 0) {
                        sendToMaster(response);
                    }
                }).start();
            }
        })).start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var res = protocol.propChanged(evt);
        if(res.length > 0) {
            sendToMaster(res);
        }
    }

    private void sendToMaster(byte[] data) {
        getSerialPort().writeBytes(data, data.length);
    }
}
