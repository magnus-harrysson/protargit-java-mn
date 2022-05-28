package com.harrys_it.ots.ports.utils;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.GpioMode;
import com.harrys_it.ots.core.model.TargetMode;
import com.harrys_it.ots.core.model.mcu.McuBroadcastMessage;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuEvent;
import com.harrys_it.ots.core.service.SettingService;
import com.harrys_it.ots.facade.GpioFacade;
import com.harrys_it.ots.facade.PcbFacade;
import com.harrys_it.ots.facade.TargetStatusFacade;
import com.harrys_it.ots.ports.utils.ProtocolContract.MCU_EVENT;
import com.harrys_it.ots.ports.utils.ProtocolContract.RESPONSE_TYPE;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;

import static com.harrys_it.ots.ports.utils.LogBuilderBluetoothAndWebsocket.buildLogOut;

@Singleton
public class BluetoothAndWebsocketKonverter {
    private final PcbFacade pcbFacade;
    private final GpioFacade gpioFacade;
    private final ResourceMapper mapper;
    private final SettingService settingService;
    private final TargetStatusFacade targetStatusFacade;

    private static final Logger log = LoggerFactory.getLogger(BluetoothAndWebsocketKonverter.class);

    public BluetoothAndWebsocketKonverter(PcbFacade pcbFacade,
                                          GpioFacade gpioFacade,
                                          ResourceMapper mapper,
                                          SettingService settingService,
                                          TargetStatusFacade targetStatusFacade) {
        this.pcbFacade = pcbFacade;
        this.gpioFacade = gpioFacade;
        this.mapper = mapper;
        this.settingService = settingService;
        this.targetStatusFacade = targetStatusFacade;
    }

    public byte[] handleDataFromMaster(byte[] data) {

        var inCommand = ProtocolContract.IN_COMMAND.fromByte(data[0]);

        byte[] response = new byte[]{};
        switch (inCommand) {
            case MODE_STOP -> {
                pcbFacade.startMode(TargetMode.STOP);
                response = new byte[]{
                        RESPONSE_TYPE.RESPONSE.getValue(),
                        inCommand.getValue(),
                        ProtocolContract.RESPONSE_STATE.OK.getValue()
                };
            }
            case MODE_HOME -> {
                pcbFacade.startMode(TargetMode.HOME);
                response = new byte[]{
                        RESPONSE_TYPE.RESPONSE.getValue(),
                        inCommand.getValue(),
                        ProtocolContract.RESPONSE_STATE.OK.getValue()
                };
            }
            case MODE_FLIP_AUTO -> {
                pcbFacade.startMode(TargetMode.FLIP_AUTO);
                response = new byte[]{
                        RESPONSE_TYPE.RESPONSE.getValue(),
                        inCommand.getValue(),
                        ProtocolContract.RESPONSE_STATE.OK.getValue()
                };
            }
            case MODE_TWIST_AUTO -> {
                pcbFacade.startMode(TargetMode.TWIST_AUTO);
                response = new byte[]{
                        RESPONSE_TYPE.RESPONSE.getValue(),
                        inCommand.getValue(),
                        ProtocolContract.RESPONSE_STATE.OK.getValue()
                };
            }
            case GPIO -> {
                byte pin = data[1];
                byte mode = data[2];
                byte timeMSB = data[3];
                byte timeLSB = data[4];
                var res = gpio(pin, mode, timeMSB, timeLSB);
                if(res) {
                    response = new byte[]{
                            RESPONSE_TYPE.RESPONSE.getValue(),
                            inCommand.getValue(),
                            ProtocolContract.RESPONSE_STATE.OK.getValue()
                    };
                } else {
                    response = new byte[]{
                            RESPONSE_TYPE.RESPONSE.getValue(),
                            inCommand.getValue(),
                            ProtocolContract.RESPONSE_STATE.ERROR.getValue()
                    };
                }
            }
            case MCU -> {
                var mcuCmd = McuCommand.fromInt(Byte.toUnsignedInt(data[1]));
                var mcuData = mapper.convertTwoBytesToOneInt(data[2], data[3]);
                var mcuEvent = new McuEvent(mcuCmd, mcuData);
                Integer resFromMcu = pcbFacade.sendToMcu(mcuEvent);
                var resData = mcuCommand(resFromMcu);
                if(resFromMcu > 0) {
                    response = new byte[]{
                            RESPONSE_TYPE.RESPONSE.getValue(),
                            inCommand.getValue(),
                            ProtocolContract.RESPONSE_STATE.OK.getValue(),
                            resData[0],
                            resData[1] };
                } else {
                    response = new byte[]{
                            RESPONSE_TYPE.RESPONSE.getValue(),
                            inCommand.getValue(),
                            ProtocolContract.RESPONSE_STATE.ERROR.getValue(),
                            resData[0],
                            resData[1] };
                }

            }
        }
        return buildResponse(response);
    }

    private boolean gpio(byte pin, byte mode, byte timeMSB, byte timeLSB) {
        var time = mapper.convertTwoBytesToOneInt(timeMSB, timeLSB);
        var gpioPin = Byte.toUnsignedInt(pin);
        if(GpioMode.ON_OFF.getValue() == mode || GpioMode.ON_TIMER.getValue() == mode) {
            return gpioFacade.setGpio(gpioPin, time);
        } else if(GpioMode.OFF_TIMER.getValue() == mode) {
            return gpioFacade.setGpio(gpioPin, (time * -1));
        } else {
            return false;
        }
    }

    private byte[] mcuCommand(Integer value) {
        byte[] resToBytes = mapper.convertPositiveIntToTwoBytes(value);
        return new byte[]{ resToBytes[0], resToBytes[1] };
    }

    public byte[] propChanged(PropertyChangeEvent evt) {
        var event = BroadcastEvent.fromByte(evt.getPropertyName());
        byte[] data = new byte[]{};
        byte[] hitSplitTime;
        switch (event) {
            case HIT -> {
                var hit = (McuBroadcastMessage) evt.getNewValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(hit.getTime());
                data = new byte[]{RESPONSE_TYPE.MCU_EVENT.getValue(), MCU_EVENT.HIT.getValue(), (byte) hit.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case STATIC_HIT -> {
                var staticHit = (McuBroadcastMessage) evt.getNewValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(staticHit.getTime());
                data = new byte[]{RESPONSE_TYPE.MCU_EVENT.getValue(), MCU_EVENT.STATIC_HIT.getValue(), (byte) staticHit.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case OTHER -> {
                var other = (McuBroadcastMessage) evt.getNewValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(other.getTime());
                data = new byte[]{RESPONSE_TYPE.MCU_EVENT.getValue(), MCU_EVENT.OTHER.getValue(), (byte) other.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case UPDATE_TARGET_STATUS -> {
                var targetInfo = targetStatusFacade.getStatus();
                data = mapper.convertTargetStatusBroadcastMessageToBytes(targetInfo);
            }
        }
        return buildResponse(data);
    }

    private byte[] buildResponse(byte[] data) {
        var settings = settingService.getManufactureSettings();
        var targetId = (byte) settings.targetId() & 0xFF;
        byte[] response = convertToSendFormatForMaster(data, (byte) targetId);
        if(log.isDebugEnabled()) {
            log.debug(buildLogOut(response));
        }
        return response;
    }

    private byte[] convertToSendFormatForMaster(byte[] data, byte targetId) {
        var response = new byte[Byte.toUnsignedInt(ProtocolContract.MAX_PACKET_SIZE)];
        response[0] = ProtocolContract.SERIAL.SEND.getValue();
        response[1] = ProtocolContract.SERIAL.DATA_LENGTH.getValue();
        response[2] = ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue();
        response[3] = ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue();

        var currentIndex = 4;
        for(byte b: data) {
            if(currentIndex==13) {
                response[currentIndex] = b;
                break;
            } else {
                response[currentIndex++] = b;
            }
        }

        response[14] = targetId;

        return response;
    }
}
