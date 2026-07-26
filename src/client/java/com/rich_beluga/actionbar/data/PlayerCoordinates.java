package com.rich_beluga.actionbar.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/*
 * Player's current block position (X/Z only - Y isn't part of the requested line).
 *
 * PORT NOTE: Yarn's PlayerEntity#getBlockX()/getBlockZ() shortcuts may or
 * may not exist verbatim in Mojang's real Entity class, so this goes
 * through the one method that is unambiguously confirmed either way:
 * Entity#blockPosition() (net.minecraft.world.entity.Entity) returning a
 * net.minecraft.core.BlockPos, then BlockPos#getX()/getZ() on that.
 */
public record PlayerCoordinates(int x, int z) {

    public static PlayerCoordinates of(Player player) {
        BlockPos pos = player.blockPosition();
        return new PlayerCoordinates(pos.getX(), pos.getZ());
    }

    public String format() {
        return "X: " + x + " Z: " + z;
    }
}
