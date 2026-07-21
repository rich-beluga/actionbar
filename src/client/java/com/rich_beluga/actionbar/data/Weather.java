package com.rich_beluga.actionbar.data;

import net.minecraft.client.world.ClientWorld;

/*
 * Computes the current weather state of the client world.
 */
public enum Weather {
    CLEAR,
    RAIN,
    THUNDER;

    /*
     * Reads the current weather from the given world.
     *
     * @param world the client world to inspect
     * @return the corresponding Weather state
     */
    public static Weather of(ClientWorld world) {
        if (world.isThundering()) {
            return THUNDER;
        } else if (world.isRaining()) {
            return RAIN;
        } else {
            return CLEAR;
        }
    }
}
