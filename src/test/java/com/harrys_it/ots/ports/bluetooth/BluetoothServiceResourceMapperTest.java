package com.harrys_it.ots.ports.bluetooth;

import com.harrys_it.ots.core.model.Movement;
import com.harrys_it.ots.core.model.TargetStatusBroadcastMessage;
import com.harrys_it.ots.ports.utils.ProtocolContract;
import com.harrys_it.ots.ports.utils.ResourceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BluetoothServiceResourceMapperTest {

    private ResourceMapper mapper;

    private static final byte ZERO = (byte) 0x00;

    @BeforeEach
    void setup() {
        mapper = new ResourceMapper();
    }

    @Test
    void testGetMovement() {
        byte hideTime = (byte) 0x05;    // 5
        byte showTime = (byte) 0x0A;    // 10
        byte runs = (byte) 0x07;        // 7
        byte hideAngle = (byte) 0x00;   // 0
        byte showAngle = (byte) 0x8C;   // 140
        byte speed = (byte) 0x63;       // 99

        Movement actual = mapper.convertMovement(
                hideTime,
                showTime,
                runs,
                hideAngle,
                showAngle,
                speed);

        Movement expected = new Movement.Builder()
                .medHideTime(5)
                .medShowTime(10)
                .medRuns(7)
                .medHideAngle(0)
                .medShowAngle(140)
                .medSpeed(99)
                .build();

        assertEquals(expected.getHideTime(), actual.getHideTime());
        assertEquals(expected.getShowTime(), actual.getShowTime());
        assertEquals(expected.getRuns(), actual.getRuns());
        assertEquals(expected.getHideAngle(), actual.getHideAngle());
        assertEquals(expected.getShowAngle(), actual.getShowAngle());
        assertEquals(expected.getSpeed(), actual.getSpeed());
    }

    @Test
    void testZonesValues() {
        byte[] actualInput = new byte[]{
                (byte)0x16,  // zone 1 (0x01), zone 2 (0x06)
                (byte)0x35,  // zone 3 (0x03), zone 4 (0x05)
                (byte)0x02}; // zone 5 (0x02)

        int[] actual = mapper.zonesValues(actualInput);

        byte[] expected = new byte[]{1, 6, 3, 5, 2};

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        assertEquals(expected[2], actual[2]);
        assertEquals(expected[3], actual[3]);
        assertEquals(expected[4], actual[4]);
    }

    @Test
    void testConvertToSendFormatForMaster() {
        byte actualInCommand = (byte) 0x01;
        byte[] actualInData = new byte[]{
                ProtocolContract.RESPONSE.OK.getValue(),
                (byte)0xFD, // Data MSB
                (byte)0xE6, // Data LSB
                (byte)0x01  // BluetoothTargetId
        };
        byte actualTargetId = (byte) 0x06;

        byte[] actual = mapper.convertToSendFormatForMaster(actualInCommand, actualInData, actualTargetId);

        byte expectedSerialCommand = ProtocolContract.SERIAL.SEND.getValue();
        byte expectedSerialDataLength = ProtocolContract.SERIAL.DATA_LENGTH.getValue();
        byte expectedAdrMSB = ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_HIGH.getValue();
        byte expectedAdrLSB = ProtocolContract.SERIAL.BLUETOOTH_SEND_ADDRESS_LOW.getValue();
        byte expectedCommandSentToSlave = ProtocolContract.IN_COMMAND.MODE_HOME.getValue();
        byte[] expectedResponseData = new byte[]{
                (byte)0xAA,
                (byte)0xFD, // MSB
                (byte)0xE6, // LSB
                (byte)0x01  // BluetoothTargetId
        };
        byte expectedTargetId = (byte) 0x06;

        byte[] expected = new byte[]{
                expectedSerialCommand,
                expectedSerialDataLength,
                expectedAdrMSB,
                expectedAdrLSB,
                expectedCommandSentToSlave,
                expectedResponseData[0],
                expectedResponseData[1],
                expectedResponseData[2],
                expectedResponseData[3],
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                expectedTargetId
        };

        for(int i=0; i<actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void testConvertIntToTwoBytes() {
        Integer actualInInt = 64998;
        byte[] actual = mapper.convertPositiveIntToTwoBytes(actualInInt);

        byte[] expected = new byte[]{
                (byte)0xFD, //MSB
                (byte)0xE6  //LSB
        };

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    @Test
    void testConvertTwoBytesToOneInt() {
        var actualData_1 = new byte[]{(byte)0xFD, (byte)0xE6};
        var actualData_2 = new byte[]{(byte)0x00, (byte)0x5A};
        var actualData_3 = new byte[]{(byte)0x01, (byte)0x00};
        var actualData_4 = new byte[]{(byte)0x00, (byte)0x01};
        var actualData_5 = new byte[]{(byte)0xFF, (byte)0xFF};

        int actual_1 = mapper.convertTwoBytesToOneInt(actualData_1[0], actualData_1[1]);
        int actual_2 = mapper.convertTwoBytesToOneInt(actualData_2[0], actualData_2[1]);
        int actual_3 = mapper.convertTwoBytesToOneInt(actualData_3[0], actualData_3[1]);
        int actual_4 = mapper.convertTwoBytesToOneInt(actualData_4[0], actualData_4[1]);
        int actual_5 = mapper.convertTwoBytesToOneInt(actualData_5[0], actualData_5[1]);

        var expected_1 = 64998;
        var expected_2 = 90;
        var expected_3 = 256;
        var expected_4 = 1;
        var expected_5 = 65535;

        assertEquals(expected_1, actual_1);
        assertEquals(expected_2, actual_2);
        assertEquals(expected_3, actual_3);
        assertEquals(expected_4, actual_4);
        assertEquals(expected_5, actual_5);
    }

    @Test
    void testConvertTargetStatusToBytes() {
        TargetStatusBroadcastMessage actualInTargetStatusBroadcastMessage = new TargetStatusBroadcastMessage(
                new boolean[] {
                        true,false,true,false // GPIO-1, 2, 3, 4
                },
                false,
                true,
                64998,
                80,
                81,
                90,
                91
        );

        byte[] actual = mapper.convertTargetStatusBroadcastMessageToBytes(actualInTargetStatusBroadcastMessage);

        byte[] expected = new byte[]{
                (byte) 0x25, // Gpio, zones active and gps enable -> true, false, true, false, false, true, x, x
                (byte) 0xFD, // Voltage MSB
                (byte) 0xE6, // Voltage LSB
                (byte) 0x50, // Twist speed
                (byte) 0x00, // Twist current angle MSB
                (byte) 0x51, // Twist current angle LSB
                (byte) 0x5A, // Flip speed
                (byte) 0x5B  // Flip angle
        };

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        assertEquals(expected[2], actual[2]);
        assertEquals(expected[3], actual[3]);
        assertEquals(expected[4], actual[4]);
        assertEquals(expected[5], actual[5]);
        assertEquals(expected[6], actual[6]);
        assertEquals(expected[7], actual[7]);
    }

    @Test
    void testConvertTargetStatusWithNegativeAngles() {
        TargetStatusBroadcastMessage actualInTargetStatusBroadcastMessage = new TargetStatusBroadcastMessage(
                new boolean[] {
                        true,false,true,false // GPIO-1, 2, 3, 4
                },
                false,
                false,
                64998,
                80,
                -1,
                90,
                -1
        );

        byte[] actual = mapper.convertTargetStatusBroadcastMessageToBytes(actualInTargetStatusBroadcastMessage);

        byte[] expected = new byte[]{
                (byte) 0x05, // Gpio, zones active and gps enable.
                (byte) 0xFD, // Voltage MSB
                (byte) 0xE6, // Voltage LSB
                (byte) 0x50, // Twist speed
                (byte) 0x00, // Twist current angle MSB
                (byte) 0x00, // Twist current angle LSB
                (byte) 0x5A, // Flip speed
                (byte) 0x00  // Flip angle
        };

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[5], actual[5]);
        assertEquals(expected[6], actual[6]);
    }

    @Test
    void testConvertTargetStatusToBytes_OtherValues() {
        TargetStatusBroadcastMessage actualInTargetStatusBroadcastMessage = new TargetStatusBroadcastMessage(
                new boolean[] {
                        false,false,false,false // GPIO-1, 2, 3, 4
                },
                false,
                false,
                2885,
                85,
                0,
                85,
                0
        );

        byte[] actual = mapper.convertTargetStatusBroadcastMessageToBytes(actualInTargetStatusBroadcastMessage);

        byte[] expected = new byte[]{
                (byte) 0x00, // Gpio, zones active and gps enable.
                (byte) 0x0B, // Voltage MSB
                (byte) 0x45, // Voltage LSB
                (byte) 0x55, // Twist speed
                (byte) 0x00, // Twist current angle MSB
                (byte) 0x00, // Twist current angle LSB
                (byte) 0x55, // Flip speed
                (byte) 0x00  // Flip angle
        };

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
        assertEquals(expected[2], actual[2]);
        assertEquals(expected[3], actual[3]);
        assertEquals(expected[4], actual[4]);
        assertEquals(expected[5], actual[5]);
        assertEquals(expected[6], actual[6]);
        assertEquals(expected[7], actual[7]);
    }
}
