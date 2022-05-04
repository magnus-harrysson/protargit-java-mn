package com.harrys_it.ots.core.model;

/**
 * Gpio commands using raspi-gpio, https://github.com/RPi-Distro/raspi-gpio
 */
public enum Os {
        GPIO_1_OUT("raspi-gpio set 25 op", false, true),
        GPIO_1_IN("raspi-gpio set 25 ip", false, true),
        GPIO_1_HIGH("raspi-gpio set 25 dh", false, true),
        GPIO_1_LOW("raspi-gpio set 25 dl", false, true),

        GPIO_2_OUT("raspi-gpio set 24 op", false, true),
        GPIO_2_IN("raspi-gpio set 24 ip", false, true),
        GPIO_2_HIGH("raspi-gpio set 24 dh", false, true),
        GPIO_2_LOW("raspi-gpio set 24 dl", false, true),

        GPIO_3_OUT("raspi-gpio set 23 op", false, true),
        GPIO_3_IN("raspi-gpio set 23 ip", false, true),
        GPIO_3_HIGH("raspi-gpio set 23 dh", false, true),
        GPIO_3_LOW("raspi-gpio set 23 dl", false, true),

        GPIO_4_OUT("raspi-gpio set 22 op", false, true),
        GPIO_4_IN("raspi-gpio set 22 ip", false, true),
        GPIO_4_HIGH("raspi-gpio set 22 dh", false, true),
        GPIO_4_LOW("raspi-gpio set 22 dl", false, true),

        WIFI_OUT("raspi-gpio set 37 op", false, true),
        WIFI_IN("raspi-gpio set 37 ip", false, true),
        WIFI_HIGH("raspi-gpio set 37 dh", false, true),
        WIFI_LOW("raspi-gpio set 37 dl", false, true),

        // Communication TX/RX for bluetooth and GPS. Set to LOW for bluetooth or HIGH for GPS
        BLUETOOTH_GPS_SHARED_OUT("raspi-gpio set 34 op", false, true),
        BLUETOOTH_GPS_SHARED_IN("raspi-gpio set 34 ip", false, true),
        BLUETOOTH_GPS_SHARED_HIGH("raspi-gpio set 34 dh", false, true),
        BLUETOOTH_GPS_SHARED_LOW("raspi-gpio set 34 dl", false, true),

        BLUETOOTH_OUT("raspi-gpio set 35 op", false, true),
        BLUETOOTH_IN("raspi-gpio set 35 ip", false, true),
        BLUETOOTH_HIGH("raspi-gpio set 35 dh", false, true),
        BLUETOOTH_LOW("raspi-gpio set 35 dl", false, true),

        GPS_OUT("raspi-gpio set 36 op", false, true),
        GPS_IN("raspi-gpio set 36 ip", false, true),
        GPS_HIGH("raspi-gpio set 36 dh", false, true),
        GPS_LOW("raspi-gpio set 36 dl", false, true),

        // PHYSICAL INPUT BUTTON
        GPIO5_BUTTON_OUT("raspi-gpio set 5 op", false, false),
        GPIO5_BUTTON_READ("raspi-gpio get 5", false, false),

        SYS_LED_ON("sudo ./ledOn.sh", false, false),
        SYS_LED_OFF("sudo ./ledOff.sh", false, false),

        BATTERY_LED_GREEN_OUT("raspi-gpio set 42 op", false, false),
        BATTERY_LED_GREEN_IN("raspi-gpio set 42 ip", false, false),
        BATTERY_LED_GREEN_HIGH("raspi-gpio set 42 dh", false, false),
        BATTERY_LED_GREEN_LOW("raspi-gpio set 42 dl", false, false),

        BATTERY_LED_RED_OUT("raspi-gpio set 43 op", false, false),
        BATTERY_LED_RED_IN("raspi-gpio set 43 ip", false, false),
        BATTERY_LED_RED_HIGH("raspi-gpio set 43 dh", false, false),
        BATTERY_LED_RED_LOW("raspi-gpio set 43 dl", false, false),

        BATTERY_LED_ENABLE_OUT("raspi-gpio set 44 op", false, false),
        BATTERY_LED_ENABLE_IN("raspi-gpio set 44 ip", false, false),
        BATTERY_LED_ENABLE_HIGH("raspi-gpio set 44 dh", false, false),
        BATTERY_LED_ENABLE_LOW("raspi-gpio set 44 dl", false, false),

        RPI_POWER_OFF("sudo poweroff -f", false, true),
        RPI_REBOOT("sudo reboot", false, true),
        PLAY_WAV_AUDIO("aplay ./audio/", true, true),
        PLAY_MP3_AUDIO("mpg123 ./audio/", true, true);


        private final String command;
        private final boolean isExtraArgumentAllowed;
        private final boolean emitValueOnChange;

        Os(String command, boolean isExtraArgumentAllowed, boolean emitValueOnChange) {
            this.command = command;
            this.isExtraArgumentAllowed = isExtraArgumentAllowed;
            this.emitValueOnChange = emitValueOnChange;
        }

        public static boolean of(Os osCmd) {
            for (Os os : Os.values()) {
                if (os == osCmd) {
                        return true;
                }
            }
            return false;
        }

        public boolean emitValueOnChange() {
            return emitValueOnChange;
        }

        public String getCommand() {
            return command;
        }

        public boolean extraArgumentAllowed() {
            return isExtraArgumentAllowed;
        }
}
