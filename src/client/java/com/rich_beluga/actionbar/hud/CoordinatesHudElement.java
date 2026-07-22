package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

/**
 * Draws the compass icon and the "X: .. Z: .." text, left-aligned
 * starting at x
 *
 * Data fetching, formatting (com.rich_beluga.actionbar.data.PlayerCoordinates#format())
 * and caching live in ActionBarHudElement; this class only knows
 * how to draw an already-computed string at an already-computed position
 */
final class CoordinatesHudElement {
    private CoordinatesHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_COORDINATES =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/compass.png");

    /*
     * param x left edge to start drawing this element at
     * param y top of the icon row, shared with every other element
     */
    static void render(DrawContext context, MinecraftClient client, String coordinatesText, int x, int y) {
        TextRenderer textRenderer = client.textRenderer;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_COORDINATES,
                x, y, 0f, 0f,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);

        int textX = x + ActionBarHudElement.ICON_SIZE + ActionBarHudElement.ICON_TEXT_GAP;
        int textY = y + (ActionBarHudElement.ICON_SIZE - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, coordinatesText, textX, textY, Colors.WHITE);
    }
}
