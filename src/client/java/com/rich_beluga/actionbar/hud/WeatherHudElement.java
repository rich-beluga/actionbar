package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import com.rich_beluga.actionbar.data.Weather;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Draws the current weather icon, left-aligned starting at {@code x}.
 * No text label - just picks the right texture for the given state.
 *
 * <p>Data fetching and caching live in {@link ActionBarHudElement}; this
 * class only knows how to draw a given, already-computed weather state.</p>
 */
final class WeatherHudElement {
    private WeatherHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_CLEAR =
            Identifier.fromNamespaceAndPath(ActionBarInfoClient.MOD_ID, "textures/gui/hud/sun.png");
    private static final Identifier ICON_RAIN =
            Identifier.fromNamespaceAndPath(ActionBarInfoClient.MOD_ID, "textures/gui/hud/rain.png");
    private static final Identifier ICON_THUNDER =
            Identifier.fromNamespaceAndPath(ActionBarInfoClient.MOD_ID, "textures/gui/hud/thunder.png");

    /**
     * @param x left edge to start drawing this element at
     * @param y top of the icon row, shared with every other element
     */
    static void render(GuiGraphicsExtractor graphics, Weather weather, int x, int y) {
        Identifier icon = switch (weather) {
            case RAIN -> ICON_RAIN;
            case THUNDER -> ICON_THUNDER;
            case CLEAR -> ICON_CLEAR;
        };

        graphics.blit(RenderPipelines.GUI_TEXTURED, icon,
                x, y, 0, 0,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);
    }
}
