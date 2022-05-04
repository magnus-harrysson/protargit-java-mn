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
import com.harrys_it.ots.ports.utils.ProtocolContract.IN_COMMAND;
import com.harrys_it.ots.ports.utils.ProtocolContract.MCU_EVENT;
import com.harrys_it.ots.ports.utils.ProtocolContract.RESPONSE;
import com.harrys_it.ots.ports.utils.ProtocolContract.RESPONSE_DATA;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import static com.harrys_it.ots.ports.utils.LogBuilderBluetoothAndWebsocket.buildLogIn;
import static com.harrys_it.ots.ports.utils.LogBuilderBluetoothAndWebsocket.buildLogOut;

@Singleton
public class BluetoothAndWebsocketProtocol {
    private final PcbFacade pcbFacade;
    private final GpioFacade gpioFacade;
    private final ResourceMapper mapper;
    private final SettingService settingService;
    private final TargetStatusFacade targetStatusFacade;

    private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothAndWebsocketProtocol.class);

    public BluetoothAndWebsocketProtocol(PcbFacade pcbFacade,
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

    public byte[] handleDataFromMaster(byte[] in, String resourceName) {
        if(isPacketNotForThisTargetId(in)) {
            return new byte[]{};
        }

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug(buildLogIn(in, resourceName));
        }

        var data = Arrays.copyOfRange(in, 4, in.length);
        var inCommand = IN_COMMAND.fromByte(data[0]);

        byte responseCode =  RESPONSE.OK.getValue();
        byte[] responseData = new byte[]{ inCommand.getValue(), 0x00, RESPONSE_DATA.ACK.getValue() };
        switch (inCommand) {
            case MODE_STOP:
                pcbFacade.startMode(TargetMode.STOP);
                break;
            case MODE_HOME:
                pcbFacade.startMode(TargetMode.HOME);
                break;

            case GPIO:
                byte pin = data[1];
                byte mode = data[2];
                byte timeMSB = data[3];
                byte timeLSB = data[4];
                gpio(pin, mode, timeMSB, timeLSB);
                break;

            case MCU:
                var mcuCmd = McuCommand.fromInt(Byte.toUnsignedInt(data[1]));
                var mcuData = mapper.convertTwoBytesToOneInt(data[2], data[3]);
                var resData = mcuCommand(new McuEvent(mcuCmd, mcuData));
                responseData[1] = resData[0];
                responseData[2] = resData[1];
                break;

            default:
                responseCode = RESPONSE.ERROR.getValue();
                responseData[2] = RESPONSE_DATA.INCORRECT_IN_COMMAND.getValue();
                break;
        }

        return buildResponse(responseCode, responseData);
    }

    private boolean isPacketNotForThisTargetId(byte[] inData) {
        var targetIdFromSettings = settingService.getManufactureSettings().targetId();
        var targetIdRemote = inData[inData.length-1];
        return targetIdFromSettings != targetIdRemote;
    }

    private void gpio(byte pin, byte mode, byte timeMSB, byte timeLSB) {
        var time = mapper.convertTwoBytesToOneInt(timeMSB, timeLSB);
        var gpioPin = Byte.toUnsignedInt(pin);
        if(GpioMode.ON_OFF.getValue() == mode || GpioMode.ON_TIMER.getValue() == mode) {
            gpioFacade.setGpio(gpioPin, time);
        } else if(GpioMode.OFF_TIMER.getValue() == mode) {
            gpioFacade.setGpio(gpioPin, (time * -1));
        }
    }

    private byte[] mcuCommand(McuEvent mcuEvent) {
        Integer resFromMcu = pcbFacade.sendToMcu(mcuEvent);
        byte[] resToBytes = mapper.convertPositiveIntToTwoBytes(resFromMcu);
        return new byte[]{ resToBytes[0], resToBytes[1] };
    }

    public byte[] propChanged(PropertyChangeEvent evt) {
        var event = BroadcastEvent.fromByte(evt.getPropertyName());
        Byte responseCode = null;
        byte[] data = new byte[]{};
        byte[] hitSplitTime;
        switch (event) {
            case HIT -> {
                var hit = (McuBroadcastMessage) evt.getNewValue();
                responseCode = RESPONSE.MCU_EVENT.getValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(hit.getTime());
                data = new byte[]{MCU_EVENT.HIT.getValue(), (byte) hit.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case STATIC_HIT -> {
                var staticHit = (McuBroadcastMessage) evt.getNewValue();
                responseCode = RESPONSE.MCU_EVENT.getValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(staticHit.getTime());
                data = new byte[]{MCU_EVENT.STATIC_HIT.getValue(), (byte) staticHit.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case OTHER -> {
                var other = (McuBroadcastMessage) evt.getNewValue();
                responseCode = RESPONSE.MCU_EVENT.getValue();
                hitSplitTime = mapper.convertPositiveIntToTwoBytes(other.getTime());
                data = new byte[]{MCU_EVENT.OTHER.getValue(), (byte) other.getCommand(), hitSplitTime[0], hitSplitTime[1]};
            }
            case UPDATE_TARGET_STATUS -> {
                var targetInfo = targetStatusFacade.getStatus();
                responseCode = RESPONSE.TARGET_INFO.getValue();
                data = mapper.convertTargetStatusBroadcastMessageToBytes(targetInfo);
            }
            default -> {
            }
        }
        return responseCode != null && data.length > 0 ? buildResponse(responseCode, data) : new byte[]{};
    }

    private byte[] buildResponse(byte responseCode, byte[] data) {
        var targetId = (byte) settingService.getManufactureSettings().targetId() & 0xFF;
        byte[] response = mapper.convertToSendFormatForMaster(responseCode, data, (byte) targetId);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug(buildLogOut(response));
        }
        return response;
    }
}
