package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

/*
 * Draws the clock icon and time text, left-aligned starting at x.
 *
 * Data fetching, caching, and the layout math (where x/y come from) all
 * live in ActionBarHudElement - this class only knows how to draw an
 * already-computed time string at an already-computed position.
 */
final class TimeHudElement {
    private TimeHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_TIME =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/clock.png");

    /*
     * param x left edge to start drawing this element at
     * param y top of the icon row, shared with every other element
     */
    static void render(DrawContext context, MinecraftClient client, String timeText, int x, int y) {
        TextRenderer textRenderer = client.textRenderer;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TIME,
                x, y, 0f, 0f,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);

        int textX = x + ActionBarHudElement.ICON_SIZE + ActionBarHudElement.ICON_TEXT_GAP;
        int textY = y + (ActionBarHudElement.ICON_SIZE - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, timeText, textX, textY, Colors.WHITE);
    }
}
