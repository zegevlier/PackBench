package me.zegs.packbench;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PackBench implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PackBench");

    private static ArrayList<PackEvent> events = new ArrayList<>();

    private static boolean isLogging = false;

    public static boolean isLogging() {
        return isLogging;
    }

    public static void setLogging(boolean isLogging) {
        PackBench.isLogging = isLogging;
    }

    public static void log(PackEvent event) {
        if (isLogging) {
            events.add(event);
        }
    }

    public static ArrayList<PackEvent> getEvents() {
        return events;
    }

    public static void clearEvents() {
        events.clear();
    }

    @Override
    public void onInitialize() {

    }
}
