package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import com.rich_beluga.actionbar.data.Weather;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/*
 * Draws the current weather icon, left-aligned starting at x
 * No text label - just picks the right texture for the given state.
 *
 * Data fetching and caching live in ActionBarHudElement; this
 * class only knows how to draw a given, already-computed weather state
 */
final class WeatherHudElement {
    private WeatherHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_CLEAR =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/sun.png");
    private static final Identifier ICON_RAIN =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/rain.png");
    private static final Identifier ICON_THUNDER =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/thunder.png");

    /**
     * param x left edge to start drawing this element at
     * param y top of the icon row, shared with every other element
     */
    static void render(DrawContext context, MinecraftClient client, Weather weather, int x, int y) {
        Identifier icon = switch (weather) {
            case RAIN -> ICON_RAIN;
            case THUNDER -> ICON_THUNDER;
            case CLEAR -> ICON_CLEAR;
        };

        context.drawTexture(RenderPipelines.GUI_TEXTURED, icon,
                x, y, 0f, 0f,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);
    }
}
