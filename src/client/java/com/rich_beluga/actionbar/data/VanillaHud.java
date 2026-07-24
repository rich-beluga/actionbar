package com.rich_beluga.actionbar.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;

/*
 * Figures out whether vanilla is currently drawing anything extra right
 * above the hotbar - the XP bar and the health/hunger/armor rows - so
 * ActionBarHudElement knows when to lift its own line a bit higher to
 * avoid overlapping them.
 *
 * All of it - health, hunger, armor AND the XP bar - is hidden together
 * in Creative/Spectator, regardless of how much XP the player actually
 * has: "In Creative mode, the health, hunger, oxygen, experience and
 * armor bars are hidden." (Minecraft Wiki, Heads-up display). So a
 * single check is enough: GameMode#isSurvivalLike() (net.minecraft.world.GameMode)
 * is true for SURVIVAL/ADVENTURE and false for CREATIVE/SPECTATOR, which
 * matches exactly.
 */
public final class VanillaHud {
    private VanillaHud() {
        // Utility holder class, never instantiated.
    }

    public static boolean hasExtraHud(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (player == null || interactionManager == null) {
            return false;
        }

        return interactionManager.getCurrentGameMode().isSurvivalLike();
    }
}
