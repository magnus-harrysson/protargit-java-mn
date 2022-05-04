package com.harrys_it.ots.core.model.other;

import com.harrys_it.ots.core.utils.McuSerialDataConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class McuSerialDataConverterTest {

    @Test
    void convertToASCII() {
        byte[] expected = new byte[]{0x02,0x31,0x36,0x3A,0x31,0x35,0x03};
        byte[] actual = McuSerialDataConverter.convertToASCII(16,15, 16);
        assertArrayEquals(expected,actual,"Convert one integer to two hex ascii");

        expected = new byte[]{0x02,0x34,0x37,0x3A,0x32,0x38,0x30,0x30,0x03};
        actual = McuSerialDataConverter.convertToASCII(47,2800, 16);
        assertArrayEquals(expected,actual,"Convert one integer to two hex ascii");
    }

    @Test
    void commandFromAsciiArrayToInt() {
        int expected = 11;
        int actual = McuSerialDataConverter.commandToInt(new byte[]{0x02,0x31,0x31,0x3a,0x36,0x30,0x03});
        assertEquals(expected,actual,"Convert two hex to one int value");

        expected = 47;
        actual = McuSerialDataConverter.commandToInt(new byte[]{0x02,0x34,0x37,0x3a,0x36,0x30,0x03});
        assertEquals(expected,actual,"Convert two hex to one int value");
    }

    @Test
    void dataFromAsciiArrayToInt() {
        int expected = 60;
        int actual = McuSerialDataConverter.dataToInt(new byte[]{0x02,0x31,0x31,0x3a,0x36,0x30,0x03});
        assertEquals(expected,actual,"Convert two hex to one int value");

        expected = 2800;
        actual = McuSerialDataConverter.dataToInt(new byte[]{0x02,0x31,0x31,0x3a,0x32,0x38,0x30,0x30,0x03});
        assertEquals(expected,actual,"Convert two hex to one int value");
    }
}
