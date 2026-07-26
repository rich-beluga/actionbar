package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Draws the compass icon and the "X: .. Z: .." text, left-aligned
 * starting at {@code x}.
 *
 * <p>Data fetching, formatting ({@link com.rich_beluga.actionbar.data.PlayerCoordinates#format()})
 * and caching live in {@link ActionBarHudElement}; this class only knows
 * how to draw an already-computed string at an already-computed position.</p>
 */
final class CoordinatesHudElement {
    private CoordinatesHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_COORDINATES =
            Identifier.fromNamespaceAndPath(ActionBarInfoClient.MOD_ID, "textures/gui/hud/compass.png");

    /**
     * @param x left edge to start drawing this element at
     * @param y top of the icon row, shared with every other element
     */
    static void render(GuiGraphicsExtractor graphics, Font font, String coordinatesText, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON_COORDINATES,
                x, y, 0, 0,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);

        int textX = x + ActionBarHudElement.ICON_SIZE + ActionBarHudElement.ICON_TEXT_GAP;
        int textY = y + (ActionBarHudElement.ICON_SIZE - font.lineHeight) / 2;
        graphics.text(font, coordinatesText, textX, textY, 0xFFFFFFFF, true);
    }
}
