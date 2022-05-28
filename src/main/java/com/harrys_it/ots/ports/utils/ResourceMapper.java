package com.harrys_it.ots.ports.utils;

import com.harrys_it.ots.core.model.Movement;
import com.harrys_it.ots.core.model.TargetStatusBroadcastMessage;
import jakarta.inject.Singleton;

@Singleton
public class ResourceMapper {
    /**
     * @param showTime time in show position. 1 byte (0-255)
     * @param hideTime time in hide position. 1 byte (0-255)
     * @param runs number of runs. 1 byte (0-255)
     * @param hideAngle angle when hiding. 1 byte (0-255)
     * @param showAngle angle when showing. 1 byte (0-255)
     * @param speed movement speed. 1 byte (0-255)
     */
    public Movement convertMovement(byte hideTime,
                                           byte showTime,
                                           byte runs,
                                           byte hideAngle,
                                           byte showAngle,
                                           byte speed) {
        return new Movement.Builder()
                .medHideTime(Byte.toUnsignedInt(hideTime))
                .medShowTime(Byte.toUnsignedInt(showTime))
                .medRuns(Byte.toUnsignedInt(runs))
                .medHideAngle(Byte.toUnsignedInt(hideAngle))
                .medShowAngle(Byte.toUnsignedInt(showAngle))
                .medSpeed(Byte.toUnsignedInt(speed))
                .build();
    }

    /**
     * Convert 3 incoming zone values to 5 int zone values internally
     * @param zoneValue 3 bytes with zone values
     *                  byte[0]=zone1 & zone2,
     *                  byte[1]=zone3 & zone4
     *                  byte[2]=zone5
     * @return an array with 5 zone values as int.
     */
    public int[] zonesValues(byte[] zoneValue) {
        var zoneValues = new int[5];
        zoneValues[1] = (zoneValue[0] & 0x0F);      // LOW 4 bits   //ZONE2
        zoneValues[0] = (zoneValue[0] & 0xFF) >> 4; // HIGH 4 bits  //ZONE1
        zoneValues[3] = (zoneValue[1] & 0x0F);      // LOW 4 bits   //ZONE4
        zoneValues[2] = (zoneValue[1] & 0xFF) >> 4; // HIGH 4 bits  //ZONE3
        zoneValues[4] = (zoneValue[2] & 0x0F);      // LOW 4 bits   //ZONE5
        return zoneValues;
    }

    /**
     * Convert positive integer to two bytes
     * @param res Positive integer value between 0-65535
     * @return integer as two bytes array with [0]=MSB [1]=LSB
     */
    public byte[] convertPositiveIntToTwoBytes(Integer res) {
        var result = new byte[2];
        if (res >= 0) {
            result[0] = (byte) ((res >> 8) & 0xffff); //MSB
            result[1] = (byte) (res & 0xffff); // LSB
        }
        return result;
    }

    public int convertTwoBytesToOneInt(byte dataMSB, byte dataLSB) {
        return ((dataMSB & 0xFF) << 8) + (dataLSB & 0xFF);
    }

    public byte[] convertTargetStatusBroadcastMessageToBytes(TargetStatusBroadcastMessage targetStatusBroadcastMessage) {
        var resTargetInfo = new byte[10];

        // byte 0 - Response type.
        resTargetInfo[0] = ProtocolContract.RESPONSE_TYPE.TARGET_INFO.getValue();

        // byte 1 - gpio's, zones- and gps active.
        if(targetStatusBroadcastMessage.gpioHigh()[0]) { resTargetInfo[1] += (0x01 & 0xFF); } // 0000 000x
        if(targetStatusBroadcastMessage.gpioHigh()[1]) { resTargetInfo[1] += (0x02 & 0xFF); } // 0000 00x0
        if(targetStatusBroadcastMessage.gpioHigh()[2]) { resTargetInfo[1] += (0x04 & 0xFF); } // 0000 0x00
        if(targetStatusBroadcastMessage.gpioHigh()[3]) { resTargetInfo[1] += (0x08 & 0xFF); } // 0000 x000
        if(targetStatusBroadcastMessage.zonesActive()) { resTargetInfo[1] += (0x10 & 0xFF); }  // 000x 0000
        if(targetStatusBroadcastMessage.gpsEnable()) { resTargetInfo[1] += (0x20 & 0xFF); } // 00x0 0000
        // Space for two more bit. 0x00 0000 (0x40 & 0xFF) and x000 0000 (0x80 & 0xFF)

        // byte 2 & 3 - voltage
        byte[] voltageAsBytes = convertPositiveIntToTwoBytes(targetStatusBroadcastMessage.voltage());
        resTargetInfo[2] = voltageAsBytes[0];
        resTargetInfo[3] = voltageAsBytes[1];

        // byte 4 - twist speed
        resTargetInfo[4] = (byte) (targetStatusBroadcastMessage.twistMotorSpeed() & 0xFF);

        // byte 5 & 6 - twist current angle
        byte[] twistCurrentAngleAsBytes = convertPositiveIntToTwoBytes(targetStatusBroadcastMessage.twistMotorCurrentAngle());
        resTargetInfo[5] = twistCurrentAngleAsBytes[0];
        resTargetInfo[6] = twistCurrentAngleAsBytes[1];

        // byte 7 - flip speed
        resTargetInfo[7] = (byte) (targetStatusBroadcastMessage.flipMotorSpeed() & 0xFF);

        // byte 8 - flip current angle
        var flipAngle = (byte) (targetStatusBroadcastMessage.flipMotorCurrentAngle() & 0xFF);
        resTargetInfo[8] = flipAngle >= 0 ? flipAngle : 0;
        return resTargetInfo;
    }
}
