package com.harrys_it.ots.ports.utils;

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

    @BeforeEach
    void setUp() {
        konverter = new BluetoothAndWebsocketKonverter(pcbFacade, gpioFacade, mapper, settingService, targetStatusFacade);
    }

    @Test
    void handleDataFromMaster() {
        var inKonverter = new byte[]{
                ProtocolContract.SERIAL.RECEIVE.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),
                ProtocolContract.IN_COMMAND.MCU.getValue(),
                0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02};
        var settings = new ManufactureSettings(
                0,
                0,
                0,
                0,
                0,
                0,
                0,0,
                0,0,0, 2);
        var inPcbFacade = new byte[]{
                (byte) 0xff,(byte) 0xff};
        var expected = new byte[]{
                ProtocolContract.SERIAL.SEND.getValue(),
                ProtocolContract.SERIAL.DATA_LENGTH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue(),
                ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue(),

                (byte) 0xAA,
                (byte) 0x07,
                (byte) 0xFF,
                (byte) 0xFF,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                (byte) 0x02
        };

        when(mapper.convertPositiveIntToTwoBytes(any())).thenReturn(inPcbFacade);
        when(pcbFacade.sendToMcu(any())).thenReturn(65535);
        when(settingService.getManufactureSettings()).thenReturn(settings);

        var actual = konverter.handleDataFromMaster(inKonverter);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void propChanged() {
    }
}
