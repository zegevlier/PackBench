package me.zegs.packbench;

import java.text.SimpleDateFormat;

public class PackEvent {

    public enum EventType {
        LOAD_START,
        LOAD_END,
        LOAD_ERROR,
    }

    private final EventType type;

    public EventType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    private final long timestamp;
    private final String message;

    PackEvent(EventType type) {
        this.type = type;
        this.timestamp = System.nanoTime();
        this.message = null;
    }

    public PackEvent(EventType type, String message) {
        this.type = type;
        this.timestamp = System.nanoTime();
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %s", timestamp, type, message);
    }
}
