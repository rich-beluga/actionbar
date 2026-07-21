package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.ActionBarInfoClient;
import com.rich_beluga.actionbar.data.Weather;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

/**
 * Renders the current weather icon, left-aligned to the screen's horizontal
 * center so it lines up with TimeHudElement
 */
public final class WeatherHudElement {
    private WeatherHudElement() {
        // Utility holder class, never instantiated.
    }

    private static final Identifier ICON_CLEAR =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/sun.png");
    private static final Identifier ICON_RAIN =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/rain.png");
    private static final Identifier ICON_THUNDER =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/thunder.png");

    private static final int ICON_SIZE = 16;
    private static final int CENTER_GAP = 3;
    private static final int HOTBAR_GAP = 6;
    private static final int HOTBAR_HEIGHT = 22;

    private static final long UPDATE_INTERVAL_MS = 2000L;

    private static long lastUpdateMs = 0L;
    private static Weather cachedWeather = Weather.CLEAR;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if (world == null || client.player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= UPDATE_INTERVAL_MS) {
            lastUpdateMs = now;
            cachedWeather = Weather.of(world);
        }

        drawIcon(context, client, cachedWeather);
    }

    private static void drawIcon(DrawContext context, MinecraftClient client, Weather weather) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Left-align so the icon starts just right of the screen's horizontal center.
        int x = screenWidth / 2 + CENTER_GAP;

        int hotbarTop = screenHeight - HOTBAR_HEIGHT;
        int y = hotbarTop - HOTBAR_GAP - ICON_SIZE;

        Identifier icon = switch (weather) {
            case RAIN -> ICON_RAIN;
            case THUNDER -> ICON_THUNDER;
            case CLEAR -> ICON_CLEAR;
        };

        context.drawTexture(RenderPipelines.GUI_TEXTURED, icon,
                x, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }
}
