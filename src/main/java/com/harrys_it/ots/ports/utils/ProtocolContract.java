package com.harrys_it.ots.ports.utils;

public class ProtocolContract {
    private ProtocolContract() { /* hide */ }

    public static final byte MAX_PACKET_SIZE = (byte) 0x0F; // 15
    private static final String EXCEPTION_TEXT = "No constant with value found for:";

    public enum SERIAL {
        SEND((byte) 0xF0),
        RECEIVE((byte) 0xF2),
        DATA_LENGTH((byte) 0x0D),
        BLUETOOTH_SEND_ADDRESS_HIGH((byte) 0xFF),
        BLUETOOTH_SEND_ADDRESS_LOW((byte) 0xFF);

        private final byte value;
        SERIAL(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    public enum IN_COMMAND {
        MODE_STOP((byte) 0x00),
        MODE_HOME((byte) 0x01),
        GPIO((byte) 0x06),
        MCU((byte) 0x07);

        private final byte value;
        IN_COMMAND(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static IN_COMMAND fromByte(byte inCommand) {
            for (IN_COMMAND i : IN_COMMAND.values()) {
                if (i.value == inCommand) {
                    return i;
                }
            }
            throw new IllegalArgumentException(EXCEPTION_TEXT + inCommand);
        }
    }

    public enum RESPONSE {
         OK((byte) 0xAA),
         MCU_EVENT((byte) 0xBB),
         TARGET_INFO((byte) 0xCC),
         ERROR((byte) 0xEE);

        private final byte value;
        RESPONSE(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static RESPONSE fromByte(byte response) {
            for (RESPONSE r : RESPONSE.values()) {
                if (r.value == response) {
                    return r;
                }
            }
            throw new IllegalArgumentException(EXCEPTION_TEXT + response);
        }
    }

    public enum RESPONSE_DATA {
        ACK((byte) 0x01),
        INCORRECT_IN_COMMAND((byte) 0xE0);

        private final byte value;
        RESPONSE_DATA(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    public enum MCU_EVENT {
        HIT((byte) 0x00),
        STATIC_HIT((byte) 0x01),
        OTHER((byte) 0x02);

        private final byte value;
        MCU_EVENT(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }
}
