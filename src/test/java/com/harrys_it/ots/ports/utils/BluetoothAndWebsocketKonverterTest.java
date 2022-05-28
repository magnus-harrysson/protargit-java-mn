package com.harrys_it.ots.ports.utils;

import com.harrys_it.ots.core.model.GpioMode;
import com.harrys_it.ots.core.model.mcu.McuCommand;
import com.harrys_it.ots.core.model.mcu.McuError;
import com.harrys_it.ots.core.model.settings.ManufactureSettings;
import com.harrys_it.ots.core.service.SettingService;
import com.harrys_it.ots.facade.GpioFacade;
import com.harrys_it.ots.facade.PcbFacade;
import com.harrys_it.ots.facade.TargetStatusFacade;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
class BluetoothAndWebsocketKonverterTest {

    @Mock
    private PcbFacade pcbFacade;
    @Mock
    private GpioFacade gpioFacade;
    @Mock
    private ResourceMapper mapper;
    @Mock
    private SettingService settingService;
    @Mock
    private TargetStatusFacade targetStatusFacade;

    private BluetoothAndWebsocketKonverter konverter;

    private static final byte ZERO = (byte) 0x00;
    private static final byte ANY_VALUE = (byte) 0x00;
    private static final ManufactureSettings SETTINGS = new ManufactureSettings(
            0,
            0,
            0,
            0,
            0,
            0,
            0,0,
            0,0,0, 2);

    @BeforeEach
    void setUp() {
        konverter = new BluetoothAndWebsocketKonverter(pcbFacade, gpioFacade, mapper, settingService, targetStatusFacade);
    }

    @Test
    void handleDataFromMaster_MODE_STOP() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MODE_STOP.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                (byte) SETTINGS.targetId() };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MODE_STOP.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_MODE_HOME() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MODE_HOME.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                (byte) SETTINGS.targetId() };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MODE_HOME.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_MODE_FLIP_AUTO() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MODE_FLIP_AUTO.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                (byte) SETTINGS.targetId() };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MODE_FLIP_AUTO.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_MODE_TWIST_AUTO() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MODE_TWIST_AUTO.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                (byte) SETTINGS.targetId() };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MODE_TWIST_AUTO.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                ANY_VALUE,
                ANY_VALUE,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_GPIO_OK() {
        byte pin = 0x01;
        byte mode = GpioMode.ON_OFF.getValue();
        byte timeMSB = 0x00;
        byte timeLSB = 0x0A; // 10 sec
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.GPIO.getValue(),
                pin,
                mode,
                timeMSB,
                timeLSB,
                (byte) SETTINGS.targetId() };

        when(mapper.convertTwoBytesToOneInt(timeMSB, timeLSB)).thenReturn(10);
        when(gpioFacade.setGpio(1, 10)).thenReturn(true);
        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.GPIO.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_GPIO_ERROR() {
        byte pin = 0x0A;
        byte mode = GpioMode.ON_OFF.getValue();
        byte timeMSB = 0x00;
        byte timeLSB = 0x0A; // 10 sec
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.GPIO.getValue(),
                pin,
                mode,
                timeMSB,
                timeLSB,
                (byte) SETTINGS.targetId() };

        when(mapper.convertTwoBytesToOneInt(timeMSB, timeLSB)).thenReturn(10);
        when(gpioFacade.setGpio(10, 10)).thenReturn(false);
        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.GPIO.getValue(),
                ProtocolContract.RESPONSE_STATE.ERROR.getValue(),
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }









    @Test
    void handleDataFromMaster_MCU_PING_OK() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MCU.getValue(),
                (byte) McuCommand.PING.getValue(),
                ANY_VALUE,
                (byte) SETTINGS.targetId() };
        var pcbResponse = new byte[]{ (byte) 0xFF,(byte) 0xEF };
        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MCU.getValue(),
                ProtocolContract.RESPONSE_STATE.OK.getValue(),
                (byte) 0xFF, // DATA
                (byte) 0xEF, // DATA
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);
        when(mapper.convertPositiveIntToTwoBytes(any())).thenReturn(pcbResponse);
        when(pcbFacade.sendToMcu(any())).thenReturn(65535);

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void handleDataFromMaster_MCU_PING_ERROR_SERIAL_CLOSE() {
        var in = new byte[]{
                ProtocolContract.IN_COMMAND.MCU.getValue(),
                (byte) McuCommand.PING.getValue(),
                ANY_VALUE,
                (byte) SETTINGS.targetId() };
        var pcbResponse = new byte[]{ (byte) 0xFF,(byte) 0x9C };

        when(settingService.getManufactureSettings()).thenReturn(SETTINGS);
        when(mapper.convertPositiveIntToTwoBytes(any())).thenReturn(pcbResponse);
        when(pcbFacade.sendToMcu(any())).thenReturn(McuError.SERIAL_CLOSED.getValue());

        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.RESPONSE_TYPE.RESPONSE.getValue(),
                ProtocolContract.IN_COMMAND.MCU.getValue(),
                ProtocolContract.RESPONSE_STATE.ERROR.getValue(),
                (byte) 0xFF, // DATA
                (byte) 0x9C, // DATA
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02 };

        var actual = konverter.handleDataFromMaster(in);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void propChanged() {
    }
}
