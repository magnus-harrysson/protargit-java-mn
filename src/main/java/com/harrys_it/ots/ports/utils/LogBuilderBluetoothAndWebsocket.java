package com.harrys_it.ots.ports.utils;

public class LogBuilderBluetoothAndWebsocket {

    private LogBuilderBluetoothAndWebsocket() { /* Hide*/ }

    public static String buildLogIn(byte[] data, String resourceName) {
        StringBuilder sb = appendSerial("IN " + resourceName + " ", data);
        sb.append("Data-part:{ InCommand:");
        sb.append(ProtocolContract.IN_COMMAND.fromByte(data[4]).name()).append(" Data:");
        for(int i = 5; i < data.length-1; i++) {
            sb.append(Byte.toUnsignedInt(data[i])).append(",");
        }
        sb.append(" TargetId:").append(data[data.length-1]).append(" }");

        return sb.toString();
    }

    public static String buildLogOut(byte[] data) {
        StringBuilder sb = appendSerial("OUT", data);

        sb.append("Data-part:{ Response:");
        sb.append(ProtocolContract.RESPONSE.fromByte(data[4]).name());
        sb.append(" Data:");
        sb.append(Byte.toUnsignedInt(data[5])).append(",");
        for(int i = 6; i < data.length-1; i++) {
            sb.append(Byte.toUnsignedInt(data[i])).append(",");
        }
        sb.append(" TargetId:").append(data[data.length-1]).append(" }");

        return sb.toString();
    }

    public static StringBuilder appendSerial(String type, byte[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" Serial-part:{ ");
        sb.append(Byte.toUnsignedInt(data[0])).append(",");
        sb.append(Byte.toUnsignedInt(data[1])).append(",");
        sb.append(Byte.toUnsignedInt(data[2])).append(",");
        sb.append(Byte.toUnsignedInt(data[3])).append(" } ");
        return sb;
    }
}
