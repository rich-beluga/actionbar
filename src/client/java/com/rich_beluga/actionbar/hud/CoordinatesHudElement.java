package com.rich_beluga.actionbar.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/*
 * Renders the player's X/Z coordinates.
 *
 * Not implemented yet - com.rich_beluga.actionbar.data.PlayerCoordinates
 * already exposes the data, but this element is intentionally a no-op for now
 * and is not registered in com.rich_beluga.actionbar.ActionBarInfoClient
 */
public final class CoordinatesHudElement {
    private CoordinatesHudElement() {
        // Utility holder class, never instantiated.
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        // TODO: render PlayerCoordinates once the layout is decided.
    }
}
