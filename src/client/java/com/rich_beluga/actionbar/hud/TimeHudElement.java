package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/*
 * Draws the clock icon and time text, left-aligned starting at x.
 *
 * Data fetching, caching, and the layout math (where x/y come from) all
 * live in ActionBarHudElement - this class only knows how to draw an
 * already-computed time string at an already-computed position.
 *
 * PORT NOTE: DrawContext#drawTexture(RenderPipeline, Identifier, x, y, u, v,
 * width, height, textureWidth, textureHeight) -> Mojang's real method name
 * for the exact same overload is GuiGraphicsExtractor#blit(...) - same
 * parameter order. RenderPipelines moved from net.minecraft.client.gl to
 * net.minecraft.client.renderer.RenderPipelines.
 */
final class TimeHudElement {
    private TimeHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_TIME =
            Identifier.fromNamespaceAndPath(ActionBarInfoClient.MOD_ID, "textures/gui/hud/clock.png");

    /**
     * @param x left edge to start drawing this element at
     * @param y top of the icon row, shared with every other element
     */
    static void render(GuiGraphicsExtractor graphics, Font font, String timeText, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON_TIME,
                x, y, 0, 0,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE,
                ActionBarHudElement.ICON_SIZE, ActionBarHudElement.ICON_SIZE);

        int textX = x + ActionBarHudElement.ICON_SIZE + ActionBarHudElement.ICON_TEXT_GAP;
        int textY = y + (ActionBarHudElement.ICON_SIZE - font.lineHeight) / 2;

        /*
         * GuiGraphicsExtractor#text(Font, String, x, y, color, shadow) -
         * merges Yarn's drawText/drawTextWithShadow into one method with an
         * explicit boolean. Color must be full ARGB (0xFFFFFFFF) as of
         * 1.21.6+, see ActionBarHudElement's WHITE constant/comment.
         */
        graphics.text(font, timeText, textX, textY, 0xFFFFFFFF, true);
    }
}
