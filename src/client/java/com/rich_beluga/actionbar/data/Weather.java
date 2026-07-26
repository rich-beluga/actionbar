package com.rich_beluga.actionbar.data;

import net.minecraft.client.multiplayer.ClientLevel;

/*
 * Current weather state, derived from Level#isThundering() /
 * Level#isRaining() (net.minecraft.world.level.Level, inherited by
 * ClientLevel). ClientLevel keeps these flags in sync with the server
 * every tick via the vanilla weather packets, so no extra networking is
 * needed here.
 *
 * PORT NOTE: isRaining()/isThundering() are unchanged between Yarn and
 * Mojang mappings - confirmed against ClientLevel's real method list.
 */
public enum Weather {
    CLEAR,
    RAIN,
    THUNDER;

    public static Weather of(ClientLevel level) {
        if (level.isThundering()) {
            return THUNDER;
        }
        if (level.isRaining()) {
            return RAIN;
        }
        return CLEAR;
    }
}
