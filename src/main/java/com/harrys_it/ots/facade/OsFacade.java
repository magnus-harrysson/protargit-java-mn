package com.harrys_it.ots.facade;

import com.harrys_it.ots.core.model.Os;
import com.harrys_it.ots.core.service.OsService;
import jakarta.inject.Singleton;

@Singleton
public class OsFacade {
    private final OsService os;

    public OsFacade(OsService os) {
        this.os = os;
    }

    public void shutdown() throws InterruptedException {
        os.command(Os.RPI_POWER_OFF);
    }

    public void restart() throws InterruptedException {
        os.command(Os.RPI_REBOOT);
    }

    public void setPhysicalWifiEnable(boolean value) throws InterruptedException {
        os.command(value ? Os.WIFI_OUT : Os.WIFI_IN);
    }

    public void playMp3(String fileName) {
        if(!fileName.endsWith(".mp3") && !fileName.contains(" ")) {
            throw new IllegalArgumentException("Incorrect filename. Cant contain 'space' and must end with .mp3");
        }
        new Thread(() -> {
            try {
                os.playAudioMp3(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();

    }

    public void playWav(String fileName) {
        if(!fileName.endsWith(".wav") && !fileName.contains(" ")) {
            throw new IllegalArgumentException("Incorrect filename. Cant contain 'space' and must end with .wav");
        }
        new Thread(() -> {
            try {
                os.playAudioWav(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void resetPinsToLow() {
        os.resetGPIO();
    }
}
