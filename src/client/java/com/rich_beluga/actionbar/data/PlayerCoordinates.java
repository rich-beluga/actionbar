package com.rich_beluga.actionbar.data;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Snapshot of the player's block-aligned X/Z position.
 * Rendering for this data is not implemented yet.
 */
public record PlayerCoordinates(int x, int z) {
    public static PlayerCoordinates of(PlayerEntity player) {
        return new PlayerCoordinates(
            player.getBlockX(),
            player.getBlockZ()
        );
    }
}
