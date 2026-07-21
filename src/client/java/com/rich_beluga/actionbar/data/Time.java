package com.rich_beluga.actionbar.data;

/*
 * Computes an in-game clock string from the world's time-of-day tick counter.
 */
public final class Time {
    private Time() {
        // Utility holder class, never instantiated.
    }

    /*
     * Converts a world "time of day" tick value into a "HH:mm" formatted string.
     * Minecraft's day starts at 6:00, so the raw tick value is shifted accordingly.
     *
     * param timeOfDay the raw value returned by ClientWorld#getTimeOfDay()
     * return the formatted time, e.g. "13:45"
     */
    public static String format(long timeOfDay) {
        long dayTicks = timeOfDay % 24000L;
        long shifted = (dayTicks + 6000L) % 24000L;
        int hours = (int) (shifted / 1000L);
        int minutes = (int) ((shifted % 1000L) * 60L / 1000L);
        return String.format("%02d:%02d", hours, minutes);
    }
}
