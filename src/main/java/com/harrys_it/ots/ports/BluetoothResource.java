package com.harrys_it.ots.ports;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.SerialConnection;
import com.harrys_it.ots.core.model.SerialProtocols;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.core.service.SerialProtocolService;
import com.harrys_it.ots.ports.utils.BluetoothAndWebsocketKonverter;
import com.harrys_it.ots.ports.utils.ProtocolContract;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.harrys_it.ots.ports.utils.LogBuilderBluetoothAndWebsocket.buildLogIn;

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
    private final BluetoothAndWebsocketKonverter protocol;
    private static final Logger log = LoggerFactory.getLogger(BluetoothResource.class);

    public BluetoothResource(BroadcasterService broadcasterService,
                             SerialProtocolService serialProtocolService,
                             BluetoothAndWebsocketKonverter protocol,
                             @Value("${serial.bluetooth.enable}") boolean startService,
                             @Value("${serial.bluetooth.comport}") String port) {
        super(SerialProtocols.BLUETOOTH, port, 115200, 8, serialProtocolService);
        this.broadcasterService = broadcasterService;
        this.protocol = protocol;
        if(startService) {
            log.debug("Started with COMPORT:{}", port);
            run();
        }
    }


    private void run() {
        broadcasterService.addPropertyChangeListener(this); // Listen to hits
        startListeningOnSerial();
        log.debug("Sending target information to proxy over bluetooth");
        this.propertyChange(new PropertyChangeEvent(BroadcasterService.class, BroadcastEvent.UPDATE_TARGET_STATUS.getValue(), "old-update", "new-update"));
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
                    var response = protocol.handleDataFromMaster(in);
                    if(response.length > 0) {
                        sendToMaster(response);
                        if(log.isDebugEnabled()) {
                            log.debug(buildLogIn(response, this.getClass().getName()));
                        }
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
