package com.rich_beluga.actionbar.data;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;

/*
 * Tracks whether vanilla's held-item name overlay is (probably) currently
 * showing right above the hotbar, so ActionBarHudElement can hide its own
 * line while it's up instead of drawing over it.
 *
 * Vanilla tracks this internally as a private field on InGameHud with no
 * public getter, and mixins are being avoided here, so this is our own
 * approximation: whenever the selected hotbar slot or the item sitting in
 * it changes, we assume vanilla just (re)started showing the name and keep
 * our own line hidden for DISPLAY_DURATION_MS - long enough to cover
 * vanilla's own display + fade-out.
 */
public final class HeldItemTooltip {
    private HeldItemTooltip() {
        // Utility holder class, never instantiated.
    }

    /* Roughly matches how long vanilla keeps the name visible + fading. Tune to taste. */
    private static final long DISPLAY_DURATION_MS = 2000L;

    private static int previousSlot = -1;
    private static Item previousItem = null;
    private static long hiddenUntilMs = 0L;

    /*
     * Must be called exactly once per frame - it also updates its own
     * "previous slot/item" tracking, which is how it notices a change.
     *
     * return true if our HUD should stay hidden this frame because the
     *        held item (probably) just changed.
     */
    public static boolean isShowing(ClientPlayerEntity player) {
        int slot = player.getInventory().getSelectedSlot();
        Item item = player.getInventory().getSelectedStack().getItem();

        boolean changed = slot != previousSlot || item != previousItem;
        previousSlot = slot;
        previousItem = item;

        long now = System.currentTimeMillis();
        if (changed) {
            hiddenUntilMs = now + DISPLAY_DURATION_MS;
        }
        return now < hiddenUntilMs;
    }
}
