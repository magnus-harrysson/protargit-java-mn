package com.harrys_it.ots.core.model;

public enum BroadcastEvent {
    HIT("hit"),
    STATIC_HIT("static"),
    OTHER("other"),
    UPDATE_TARGET_STATUS("update-target-status");

    private final String value;

    BroadcastEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BroadcastEvent fromByte(String broadcastEvent) {
        for (BroadcastEvent b : BroadcastEvent.values()) {
            if (b.value.equals(broadcastEvent)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with value " + broadcastEvent + " found");
    }
}
