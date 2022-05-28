package com.harrys_it.ots.ports.utils;

public class ProtocolContract {
    private ProtocolContract() { /* hide */ }

    public static final byte MAX_PACKET_SIZE = (byte) 0x0F; // 15

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
        MODE_FLIP_AUTO((byte) 0x02),
        MODE_TWIST_AUTO((byte) 0x03),
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
            throw new IllegalArgumentException("Error no IN_COMMAND with value:" + inCommand + " found");
        }
    }

    public enum RESPONSE_TYPE {
         RESPONSE((byte) 0xAA),
         MCU_EVENT((byte) 0xBB),
         TARGET_INFO((byte) 0xCC);

        private final byte value;
        RESPONSE_TYPE(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static RESPONSE_TYPE fromByte(byte response) {
            for (RESPONSE_TYPE r : RESPONSE_TYPE.values()) {
                if (r.value == response) {
                    return r;
                }
            }
            throw new IllegalArgumentException("Error no RESPONSE with value:" + response + " found");
        }
    }

    public enum RESPONSE_STATE {
        OK((byte) 0x01),
        ERROR((byte) 0x02);

        private final byte value;
        RESPONSE_STATE(byte value) {
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
