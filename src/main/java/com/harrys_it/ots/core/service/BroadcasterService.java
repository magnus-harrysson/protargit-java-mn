package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.BroadcastEvent;
import com.harrys_it.ots.core.model.mcu.McuBroadcastMessage;
import jakarta.inject.Singleton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Singleton
public class BroadcasterService {

    private McuBroadcastMessage message;
    private final PropertyChangeSupport support;

    public BroadcasterService() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void sendHit(McuBroadcastMessage mcuBroadcastMessage) {
        support.firePropertyChange(BroadcastEvent.HIT.getValue(), this.message, mcuBroadcastMessage);
        this.message = mcuBroadcastMessage;
    }

    public void sendStaticHit(McuBroadcastMessage mcuBroadcastMessage) {
        support.firePropertyChange(BroadcastEvent.STATIC_HIT.getValue(), this.message, mcuBroadcastMessage);
        this.message = mcuBroadcastMessage;
    }

    public void sendOther(McuBroadcastMessage mcuBroadcastMessage) {
        support.firePropertyChange(BroadcastEvent.OTHER.getValue(), this.message, mcuBroadcastMessage);
        this.message = mcuBroadcastMessage;
    }

    /**
     * This method will always fire an event when called. Old and new values are dummy values.
     */
    public void updateValues() {
        support.firePropertyChange(BroadcastEvent.UPDATE_TARGET_STATUS.getValue(), "old-update", "new-update");
    }
}
