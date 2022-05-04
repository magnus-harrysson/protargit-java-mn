package com.harrys_it.ots.core.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harrys_it.ots.core.model.settings.ManufactureSettings;
import com.harrys_it.ots.core.model.settings.UserSettings;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class SettingService {

    private UserSettings userSettings;
    private ManufactureSettings manufactureSettings;

    private String userSettingsUrl = "./settings/user_settings.json";
    private String manufactureSettingsUrl = "./settings/manufacture_settings.json";

    private static final String USER_SETTINGS_DEFAULT_URL = "./settings/user_settings_default.json";
    private static final String MANUFACTURE_SETTINGS_DEFAULT_URL = "./settings/manufacture_settings_default.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingService.class);
    private static final String DEBUG_MSG = "{}() fail to parse from json file :";

    public SettingService(){
        loadSettings();
    }

    public void loadSettings(){
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(userSettingsUrl))) {
            userSettings = gson.fromJson(reader, UserSettings.class);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "loadSettings", e);
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(manufactureSettingsUrl))) {
            manufactureSettings = gson.fromJson(reader, ManufactureSettings.class);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "loadSettings", e);
        }
    }

    public void saveSettings() {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(userSettingsUrl)){
            gsonBuilder.toJson(userSettings, writer);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "saveSettings", e);
        }

        try (Writer writer = new FileWriter(manufactureSettingsUrl)){
            gsonBuilder.toJson(manufactureSettings, writer);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "saveSettings", e);
        }
    }

    public void loadDefaultSettings(){
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_SETTINGS_DEFAULT_URL))) {
            userSettings = gson.fromJson(reader, UserSettings.class);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "loadDefaultSettings", e);
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(MANUFACTURE_SETTINGS_DEFAULT_URL))) {
            manufactureSettings = gson.fromJson(reader, ManufactureSettings.class);
        } catch (Exception e) {
            LOGGER.debug(DEBUG_MSG, "loadDefaultSettings", e);
        }

        saveSettings();
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public ManufactureSettings getManufactureSettings() {
        return manufactureSettings;
    }

    public void setManufactureSettings(ManufactureSettings manufactureSettings) {
        this.manufactureSettings = manufactureSettings;
    }

    public void setUserSettingsUrl(String userSettingsUrl) {
        this.userSettingsUrl = userSettingsUrl;
    }

    public void setManufactureSettingsUrl(String manufactureSettingsUrl) {
        this.manufactureSettingsUrl = manufactureSettingsUrl;
    }
}
