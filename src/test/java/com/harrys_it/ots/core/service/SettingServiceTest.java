package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.settings.ManufactureSettings;
import com.harrys_it.ots.core.model.settings.UserSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

    private SettingService settingService;
    private static final UserSettings DEFAULT_USER_SETTINGS = new UserSettings(
            0,
            5000,
            0,
            0,
            90,
            90,
            0,
            5000,
            0,
            90,
            0,
            85);

    private static final ManufactureSettings DEFAULT_MANUFACTURE_SETTINGS = new ManufactureSettings(
            3000,
            1800,
            85,
            90,
            5,
            1200,
            1200,
            85,
            90,
            5,
            5,
            0
    );

    private static final UserSettings NEW_USER_SETTINGS = new UserSettings(
            1,
            5000,
            0,
            0,
            90,
            90,
            0,
            5000,
            0,
            90,
            0,
            2);

    private static final ManufactureSettings NEW_MANUFACTURE_SETTINGS = new ManufactureSettings(
            1,
            1800,
            85,
            90,
            5,
            1200,
            1200,
            85,
            90,
            5,
            5,
            2
    );

    @BeforeEach
    void setup() {
        // Loads settings in constructor
        settingService = new SettingService();

    }

    @Test
    void showLoadUserAndManufactureSettings() {
        Assertions.assertEquals(DEFAULT_USER_SETTINGS, settingService.getUserSettings());
        Assertions.assertEquals(DEFAULT_MANUFACTURE_SETTINGS, settingService.getManufactureSettings());
    }

    @Test
    void saveAndLoadNewUserAndManufactureSettings() {
        loadNewSettings();
    }

    @Test
    void loadDefaultUserAndManufactureSettings() {
        loadNewSettings();
        settingService.loadDefaultSettings();
        Assertions.assertEquals(DEFAULT_USER_SETTINGS, settingService.getUserSettings());
        Assertions.assertEquals(DEFAULT_MANUFACTURE_SETTINGS, settingService.getManufactureSettings());
    }

    private void loadNewSettings() {
        var tempUserSettingsUrl = "./tmp_test/tmp_user_settings.json";
        var tempManufactureSettingsUrl = "./tmp_test/tmp_manufacture_settings.json";

        settingService.setUserSettings(NEW_USER_SETTINGS);
        settingService.setManufactureSettings(NEW_MANUFACTURE_SETTINGS);

        settingService.setUserSettingsUrl(tempUserSettingsUrl);
        settingService.setManufactureSettingsUrl(tempManufactureSettingsUrl);
        settingService.saveSettings();
        settingService.loadSettings();

        Assertions.assertEquals(NEW_USER_SETTINGS, settingService.getUserSettings());
        Assertions.assertEquals(NEW_MANUFACTURE_SETTINGS, settingService.getManufactureSettings());

        File file1 = new File(tempUserSettingsUrl);
        var res1 = file1.delete();
        File file2 = new File(tempManufactureSettingsUrl);
        var res2 = file2.delete();
        if(!res1 || !res2) {
            System.err.println("Couldn't delete temp files");
        }
    }
}
