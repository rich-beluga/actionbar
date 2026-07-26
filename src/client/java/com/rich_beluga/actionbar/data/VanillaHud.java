package com.rich_beluga.actionbar.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;

/*
 * Figures out whether vanilla is currently drawing anything extra right
 * above the hotbar - the XP bar and the health/hunger/armor rows - so
 * ActionBarHudElement knows how much to lift its own line to avoid
 * overlapping them.
 *
 * All of it - health, hunger, armor AND the XP bar - is hidden together
 * in Creative/Spectator, regardless of how much XP the player actually
 * has (confirmed: Minecraft Wiki, Heads-up display page).
 *
 * PORT NOTE (1.21.8 Yarn -> 26.2 Mojang mappings):
 *  - ClientPlayerInteractionManager#getCurrentGameMode() -> Mojang's real
 *    class/method are MultiPlayerGameMode#getPlayerMode()
 *    (net.minecraft.client.multiplayer.MultiPlayerGameMode)
 *  - GameMode enum -> Mojang's real name is GameType
 *    (net.minecraft.world.level.GameType). We don't rely on Yarn's
 *    isSurvivalLike() convenience method here since it isn't confirmed to
 *    exist under that name in Mojang mappings - a direct != CREATIVE/
 *    SPECTATOR check is equivalent and doesn't need it.
 *  - LivingEntity#getArmor() (Yarn) -> Mojang's real name is
 *    LivingEntity#getArmorValue() - confirmed against multiple Mojang-
 *    mapped javadoc mirrors, this one hasn't changed across versions.
 */
public final class VanillaHud {

    private VanillaHud() {
        // Utility holder class, never instantiated.
    }

    public static boolean hasExtraHud(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        MultiPlayerGameMode gameMode = minecraft.gameMode;

        if (player == null || gameMode == null) {
            return false;
        }

        GameType type = gameMode.getPlayerMode();
        return type != GameType.CREATIVE && type != GameType.SPECTATOR;
    }

    public static boolean hasArmor(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return false;
        }

        return player.getArmorValue() > 0;
    }
}
