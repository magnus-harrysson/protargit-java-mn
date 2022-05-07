package com.harrys_it.ots.ports;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.SerialConnection;
import com.harrys_it.ots.core.model.SerialProtocols;
import com.harrys_it.ots.core.model.mcu.McuBroadcastMessage;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.BroadcasterService;
import com.harrys_it.ots.core.service.SerialProtocolService;
import com.harrys_it.ots.facade.GpioFacade;
import com.harrys_it.ots.facade.OsFacade;
import com.harrys_it.ots.facade.PcbFacade;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class UsbSerialResource extends SerialConnection implements PropertyChangeListener {

    private final BroadcasterService broadcasterService;
    private final GpioFacade gpioFacade;
    private final OsFacade osFacade;
    private final PcbFacade pcbFacade;

    private static final byte START_OF_LINE = 0x43; // 'C'
    private static final byte END_OF_LINE = 0x3b; // ';'
    private static final Logger LOGGER = LoggerFactory.getLogger(UsbSerialResource.class);

    public UsbSerialResource(BroadcasterService broadcasterService,
                             GpioFacade gpioFacade,
                             OsFacade osFacade,
                             PcbFacade pcbFacade,
                             SerialProtocolService serialProtocolService) {
        super(SerialProtocols.SERIAL_USB, "/dev/ttyUSB0", 115200, 8, serialProtocolService);
        this.broadcasterService = broadcasterService;
        this.gpioFacade = gpioFacade;
        this.osFacade = osFacade;
        this.pcbFacade = pcbFacade;
    }

    @Value("${serial.usb.enable}")
    private void run(boolean startSerialUsb) {
        LOGGER.debug("{}", startSerialUsb);
        if(startSerialUsb) {
            broadcasterService.addPropertyChangeListener(this); // Listen to hits
            new Thread(() -> getSerialPort().addDataListener(new SerialPortMessageListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public byte[] getMessageDelimiter() {
                    return new byte[]{ END_OF_LINE };
                }

                @Override
                public boolean delimiterIndicatesEndOfMessage() {
                    return true;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    var delimitedMessage = event.getReceivedData();
                    if ((delimitedMessage[0] == START_OF_LINE) && (delimitedMessage[delimitedMessage.length - 1] == END_OF_LINE)) {
                        var fullResponseString = new String(delimitedMessage);
                        var cmd = getCmdFromString(fullResponseString);
                        var data = getDataFromString(fullResponseString);
                        LOGGER.debug("fromClient(), string received:{},  cmd:{},  data:{}", fullResponseString, cmd, data);
                        fromClient(fullResponseString, cmd, data);
                    }
                }
            })).start();
        }
    }

    private void fromClient(String fullString, String cmd, String data) {
        switch (cmd) {
            case "gpio20" -> {
                osFacade.resetPinsToLow();
                returnResponseNormal("50,ACK;");
            }
            case "gpio22", "gpio23", "gpio24", "gpio25" -> gpioCommand(cmd, data);
            case "audio" -> audioCommand(data);
            default -> otherCommand(fullString, cmd, data);
        }
    }

    private void gpioCommand(String cmd, String data) {
        if(!isDataANumber(data)) {
            returnResponseError();
            return;
        }

        // Get last 2 chars from inputted command e.g. Gpio22 -> 22
        var gpioPin = cmd.substring(cmd.length()-2);

        var pin = getPin(Integer.parseInt(gpioPin));
        if(pin == null) {
            return;
        }
        gpioFacade.setGpio(pin, Integer.parseInt(data));

        // 51-56 because the 20-range is used for MCU response.
        int gpioResponseNumber = (Integer.parseInt(gpioPin) + 30);
        returnResponseNormal(gpioResponseNumber + ",ACK;");
    }

    private Integer getPin(int parseInt) {
        return switch (parseInt) {
            case 22 -> 4;
            case 23 -> 3;
            case 24 -> 2;
            case 25 -> 1;
            default -> null;
        };
    }

    private void audioCommand(String data) {
        var isAudioExecuted = executeAudio(data);

        if(isAudioExecuted) {
            returnResponseNormal("60,ACK;");
        } else {
            returnResponseNormal("60,NACK;");
        }
    }

    // Any other command. Check if string is valid and data is valid, else respond with an NOT_VALID
    private void otherCommand(String fullString, String cmd, String data) {
        boolean isCommandValid = isOtherCommand(fullString);
        boolean isDataValid = isDataANumber(data);

        if(isCommandValid && isDataValid) {
            boolean ackFromMcu = sendToMcuController(Integer.parseInt(cmd), Integer.parseInt(data));
            if (ackFromMcu) {
                returnResponseNormal(cmd + ",ACK;");
            } else {
                returnResponseNormal(cmd + ",NACK;");
            }
        } else {
            returnResponseError();
        }
    }

    private void returnResponseNormal(String responseMessage) {
        var messageToClient = "RES"+ responseMessage + System.lineSeparator();
        LOGGER.debug("returnResponseNormal(), RES:{}({})", messageToClient, messageToClient.length());
        getSerialPort().writeBytes(messageToClient.getBytes(), messageToClient.getBytes().length);
    }

    private void returnResponseError() {
        var res = "NOT_VALID" + System.lineSeparator();
        LOGGER.debug("returnResponseError(), RES:{}({})", res, res.length());
        getSerialPort().writeBytes(res.getBytes(), res.getBytes().length);
    }

    private boolean isDataANumber(String data) {
        if(data == null) {
            return false;
        }
        try {
            Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isOtherCommand(String fullString) {
        return ((fullString.length() >= 6)
                && fullString.charAt(0) == 'C'
                && fullString.contains(",")
                && fullString.contains("D")
                && fullString.charAt(fullString.length() - 1) == ';');
    }

    private synchronized boolean sendToMcuController(int cmd, int data) {
        var resFromMcu = pcbFacade.sendToMcu(new McuEvent(McuCommand.fromInt(cmd), data));
        return resFromMcu >= 0;
    }

    public boolean executeAudio(String data) {
        var fileFormat = data.substring(data.length()-3);
        if (fileFormat.equals("wav")) {
            osFacade.playWav(data);
            return true;
        } else if (fileFormat.equals("mp3")) {
            osFacade.playMp3(data);
            return true;
        }
        return false;
    }

    private String getCmdFromString(String fullString) {
        var startIndex = fullString.indexOf("C") + 1;
        var stopIndex = fullString.indexOf(",");
        return fullString.substring(startIndex, stopIndex);
    }

    private String getDataFromString(String fullString) {
        var startIndex = fullString.indexOf("D") + 1;
        var stopIndex = fullString.indexOf(";");
        return fullString.substring(startIndex, stopIndex);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.isSerialPortOpen() && readBroadcastValue(evt)) {
            var mcuBroadcastModel = (McuBroadcastMessage) evt.getNewValue();
            String res = "RES" + (mcuBroadcastModel.getCommand()+11) + "," + mcuBroadcastModel.getData() + "," + getCurrentTime() + ";" + System.lineSeparator();
            getSerialPort().writeBytes(res.getBytes(), res.getBytes().length);
        }
    }

    private String getCurrentTime() {
        var timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        var time = LocalDateTime.now();
        return timeFormat.format(time);
    }

    private boolean readBroadcastValue(PropertyChangeEvent evt) {
        return (evt.getPropertyName().equals(BroadcastEvent.HIT.getValue()) ||
                evt.getPropertyName().equals(BroadcastEvent.STATIC_HIT.getValue()) ||
                evt.getPropertyName().equals(BroadcastEvent.OTHER.getValue()));
    }

}
