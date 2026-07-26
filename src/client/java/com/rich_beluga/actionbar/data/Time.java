package com.rich_beluga.actionbar.data;

import net.minecraft.client.multiplayer.ClientLevel;

/*
 * In-game clock time, derived from the overworld's day-time tick counter:
 * ticks in [0, 24000) that repeat every in-game day (24000 ticks = 20 real
 * minutes). Tick 0 of that counter is 6:00 (sunrise), not midnight - the
 * same convention the vanilla clock item's needle animation and the
 * sun/moon position use - so we shift by +6000 ticks (6 hours) before
 * converting. 1000 ticks == 1 in-game hour.
 *
 * PORT NOTE (1.21.8 Yarn -> 26.2 Mojang mappings): Level#getDayTime() was
 * removed in 26.1 when Mojang reworked time into the new "World Clock"
 * system. Confirmed against the official neoforged.net 26.1 migration
 * primer ("World Clocks and Time Markers" section):
 *   Level#getDayTime -> Level#getOverworldClockTime (not one-to-one)
 *   Level#getDefaultClockTime - "gets the clock time for the current
 *   dimension", the direct drop-in replacement used here.
 */
public final class Time {

    private Time() {
        // Utility holder class, never instantiated.
    }

    /** Reads the current dimension's clock tick from the new World Clock system. */
    public static long currentDayTicks(ClientLevel level) {
        return level.getDefaultClockTime();
    }

    public static String format(long dayTime) {
        long dayTicks = dayTime % 24000L;
        long shifted = (dayTicks + 6000L) % 24000L; // 0 == 00:00 after the shift
        int hours = (int) (shifted / 1000L);
        int minutes = (int) ((shifted % 1000L) * 60L / 1000L);
        return String.format("%02d:%02d", hours, minutes);
    }
}
