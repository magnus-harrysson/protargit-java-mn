package com.harrys_it.ots.core.model.mcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McuUtils {

    private static final Logger log = LoggerFactory.getLogger(McuUtils.class);

    private McuUtils() { /* hide */ }

    protected static boolean validateDataAndCmd(McuCommand cmd, int data) {
        return switch (cmd) {
            case PING -> data == McuDataLimit.PING.getValue();
            case FLIP_MOTOR_CURRENT_LIMIT ->
                    data >= McuDataLimit.FLIP_MOTOR_CURRENT_LIMITATION_MIN.getValue() && data <= McuDataLimit.FLIP_MOTOR_CURRENT_LIMITATION_MAX.getValue();
            case FLIP_FACTORY_CALIBRATION ->
                    data >= McuDataLimit.FLIP_FACTORY_CALIBRATION_SPEED_MIN.getValue() && data <= McuDataLimit.FLIP_FACTORY_CALIBRATION_SPEED_MAX.getValue();
            case FLIP_SPEED ->
                    data >= McuDataLimit.FLIP_SPEED_MIN.getValue() && data <= McuDataLimit.FLIP_SPEED_MAX.getValue();
            case FLIP_ANGLE ->
                    data >= McuDataLimit.FLIP_ANGLE_MIN.getValue() && data <= McuDataLimit.FLIP_ANGLE_MAX.getValue();
            case HIT_ZONES ->
                    data == McuDataLimit.HIT_ZONES_DISABLE.getValue() || data == McuDataLimit.HIT_ZONES_ENABLE.getValue();
            case TWIST_MOTOR_CURRENT_LIMIT ->
                    data >= McuDataLimit.TWIST_MOTOR_CURRENT_LIMITATION_MIN.getValue() && data <= McuDataLimit.TWIST_MOTOR_CURRENT_LIMITATION_MAX.getValue();
            case TWIST_FACTORY_CALIBRATION ->
                    data >= McuDataLimit.TWIST_FACTORY_CALIBRATION_SPEED_MIN.getValue() && data <= McuDataLimit.TWIST_FACTORY_CALIBRATION_SPEED_MAX.getValue();
            case TWIST_SPEED ->
                    data >= McuDataLimit.TWIST_SPEED_MIN.getValue() && data <= McuDataLimit.TWIST_SPEED_MAX.getValue();
            case TWIST_ANGLE ->
                    data >= McuDataLimit.TWIST_ANGLE_MIN.getValue() && data <= McuDataLimit.TWIST_ANGLE_MAX.getValue();
            case PROCESSOR_RESET -> data == McuDataLimit.PROCESSOR_RESET.getValue();
            case FLIP_CURRENT_ANGLE, TWIST_CURRENT_ANGLE, STATIC_HIT_ZONES, HIT_ZONE_1, HIT_ZONE_2, HIT_ZONE_3, HIT_ZONE_4, HIT_ZONE_5, BATTERY_CAPACITY, BATTERY_VOLTAGE, MAJOR, MINOR, BUILD, TWOCOM_BUS_START, TWOCOM_BUS_END ->
                    true;
            default -> false;
        };
    }

    public static boolean errorOrHitOrStaticHitResponse(int cmd){
        return errorResponse(cmd) || zoneStaticResponse(cmd) || zoneHitResponse(cmd);
    }

    public static boolean errorResponse(int cmd) {
        return cmd == McuCommand.STATIC_HIT_ZONES.getValue() ||
                cmd == McuCommand.EXCESS_CURRENT_DURING_MOVEMENT.getValue() ||
                cmd == McuCommand.MOTOR_TIMEOUT.getValue();
    }

    public static boolean zoneStaticResponse(int cmd) {
        return cmd == McuCommand.STATIC_ZONE_1.getValue() ||
                cmd == McuCommand.STATIC_ZONE_2.getValue() ||
                cmd == McuCommand.STATIC_ZONE_3.getValue() ||
                cmd == McuCommand.STATIC_ZONE_4.getValue() ||
                cmd == McuCommand.STATIC_ZONE_5.getValue();
    }

    public static boolean zoneHitResponse(int cmd) {
        return cmd == McuCommand.HIT_ZONE_1.getValue() ||
                cmd == McuCommand.HIT_ZONE_2.getValue() ||
                cmd == McuCommand.HIT_ZONE_3.getValue() ||
                cmd == McuCommand.HIT_ZONE_4.getValue() ||
                cmd == McuCommand.HIT_ZONE_5.getValue();
    }

    public static boolean canReturnZeroAsAck(int cmd) {
        return cmd == McuCommand.FLIP_CURRENT_ANGLE.getValue() ||
                cmd == McuCommand.TWIST_CURRENT_ANGLE.getValue() ||
                cmd == McuCommand.BATTERY_CAPACITY.getValue() ||
                cmd == McuCommand.BATTERY_VOLTAGE.getValue() ||
                cmd == McuCommand.MAJOR.getValue() ||
                cmd == McuCommand.MINOR.getValue() ||
                cmd == McuCommand.BUILD.getValue();
    }

    /**
     * Some commands will return full value instead of ACK/NACK (0/1).
      */
    public static String returnValueAsACKorNACKorInt(int cmd, Integer responseFromMcu) {
        return canReturnZeroAsAck(cmd) ? Integer.toString(responseFromMcu) : ackOrNack(responseFromMcu);
    }

    public static String ackOrNack(Integer responseFromMcu) {
        return (responseFromMcu == 1 ? "ACK" : "NACK");
    }

    /**
     * Fetch command(int) and data(int) from input String.
     * @param echoString input string converted to charArray ex. ['C' '6' ',' 'D' '9' '0' ';']
     * @return cmd and data found in string echoString as two integers
     */
    public static Integer[] convertStringToCmdIntAndDataInt(String echoString) {
        char[] values = echoString.toCharArray();
        var sbCmd = new StringBuilder();
        var sbData = new StringBuilder();

        var skip = false;

        for (char c : values) {
            if (!skip) {
                if (c == ',') {
                    skip = true;
                } else if (Character.isDigit(c)) {
                    sbCmd.append(c);
                }

            } else {
                if (c == ';') {
                    break;
                } else if(Character.isDigit(c)) {
                    sbData.append(c);
                }
            }
        }
        Integer cmd = null;
        Integer data = null;

        try {
            cmd = Integer.parseInt(sbCmd.toString());
        } catch (NumberFormatException e) {
            log.debug("Can not parse cmd as number", e);
        }

        try {
            data = Integer.parseInt(sbData.toString());
        } catch (NumberFormatException e) {
            log.debug("Can not parse data as number", e);
        }


        return new Integer[]{cmd, data};
    }

    /**
     * Fetch command(string) and data(int) from input String.
     * @param s input string converted to charArray ex. ['C' 'G' 'P' 'I' 'O' '4' ',' '3' '0' '0' ';']
     * @return cmd and data found in string s as strings
     */
    public static String[] convertStringToCmdStringAndDataInt(String s) {
        char[] charArray = s.toCharArray();
        var breakIndex = 0;

        var sbCmd = new StringBuilder();
        for (var i = 0 ; i < charArray.length ; i++){
            if (charArray[i] == ','){
                breakIndex = i;
                break;
            }
            sbCmd.append(charArray[i]);
        }

        var sbData = new StringBuilder();
        for (var i = breakIndex + 1 ; i < charArray.length ; i++){
            if (charArray[i] != ';'){
                sbData.append(charArray[i]);
            }
        }

        return new String[]{sbCmd.toString(), sbData.toString()};
    }
}
