package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import com.rich_beluga.actionbar.data.Time;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

/*
 * Renders the clock icon and the current in-game time, right-aligned to the
 * screen's horizontal center so it lines up with WeatherHudElement
 */
public final class TimeHudElement {
    private TimeHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_TIME =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/clock.png");

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_GAP = 4;
    private static final int CENTER_GAP = 3;
    private static final int HOTBAR_GAP = 6;
    private static final int HOTBAR_HEIGHT = 22;

    private static final long UPDATE_INTERVAL_MS = 2000L;

    private static long lastUpdateMs = 0L;
    private static String cachedTimeText = "--:--";

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if (world == null || client.player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= UPDATE_INTERVAL_MS) {
            lastUpdateMs = now;
            cachedTimeText = Time.format(world.getTimeOfDay());
        }

        drawLine(context, client, cachedTimeText);
    }

    private static void drawLine(DrawContext context, MinecraftClient client, String timeText) {
        TextRenderer textRenderer = client.textRenderer;

        int timeTextWidth = textRenderer.getWidth(timeText);
        int contentWidth = ICON_SIZE + ICON_TEXT_GAP + timeTextWidth;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Right-align so the block ends just left of the screen's horizontal center.
        int x = screenWidth / 2 - CENTER_GAP - contentWidth;

        int hotbarTop = screenHeight - HOTBAR_HEIGHT;
        int y = hotbarTop - HOTBAR_GAP - ICON_SIZE;
        int textY = y + (ICON_SIZE - textRenderer.fontHeight) / 2;

        int cursorX = x;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TIME,
                cursorX, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        cursorX += ICON_SIZE + ICON_TEXT_GAP;

        context.drawTextWithShadow(textRenderer, timeText, cursorX, textY, Colors.WHITE);
    }
}
