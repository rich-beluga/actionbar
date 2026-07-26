package com.rich_beluga.actionbar.data;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;

/*
 * Tracks whether vanilla's held-item name overlay is (probably) currently
 * showing right above the hotbar, so ActionBarHudElement can hide its own
 * line while it's up instead of drawing over it.
 *
 * Vanilla tracks this internally as a private field on the HUD renderer
 * with no public getter, and mixins are being avoided here, so this is
 * our own approximation: whenever the item in the player's main hand
 * changes, we assume vanilla just (re)started showing the name and keep
 * our own line hidden for DISPLAY_DURATION_MS.
 *
 * PORT NOTE (1.21.8 Yarn -> 26.2 Mojang mappings): the 1.21.8 version also
 * tracked the selected hotbar SLOT INDEX (PlayerInventory#getSelectedSlot())
 * to catch switching between two slots holding the same item type. That
 * inventory-slot API isn't confirmed under Mojang mappings here, so this
 * port only tracks the main-hand ItemStack's item identity via
 * Player#getMainHandItem() (net.minecraft.world.entity.player.Player,
 * confirmed stable name) - a minor behavior simplification, not a bug.
 */
public final class HeldItemTooltip {

    private HeldItemTooltip() {
        // Utility holder class, never instantiated.
    }

    /* Roughly matches how long vanilla keeps the name visible + fading. Tune to taste. */
    private static final long DISPLAY_DURATION_MS = 2000L;

    private static Item previousItem = null;
    private static long hiddenUntilMs = 0L;

    /*
     * Must be called exactly once per frame - it also updates its own
     * "previous item" tracking, which is how it notices a change.
     */
    public static boolean isShowing(LocalPlayer player) {
        Item item = player.getMainHandItem().getItem();

        boolean changed = item != previousItem;
        previousItem = item;

        long now = System.currentTimeMillis();
        if (changed) {
            hiddenUntilMs = now + DISPLAY_DURATION_MS;
        }
        return now < hiddenUntilMs;
    }
}
