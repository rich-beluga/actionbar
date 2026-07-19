package com.rich_beluga.actionbar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public final class TimeWeatherHudElement {
    private TimeWeatherHudElement() {
        // Utility holder class, never instantiated.
    }

    /*
     * Textures, 16x16 px, PNG, RGBA. Put your own art at these exact paths
     * (src/client/resources/assets/actionbar/textures/gui/hud/...) - the
     * ones shipped with this project are throwaway placeholders generated
     * to keep the mod runnable out of the box.
     *
     * Identifier.of(namespace, path) - confirmed Yarn usage, see the
     * official 1.21.6/1.21.7/1.21.8 changelog:
     * https://fabricmc.net/2025/06/15/1216.html
     */
    private static final Identifier ICON_TIME =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/clock.png");
    private static final Identifier ICON_CLEAR =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/sun.png");
    private static final Identifier ICON_RAIN =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/rain.png");
    private static final Identifier ICON_THUNDER =
            Identifier.of(ActionBarInfoClient.MOD_ID, "textures/gui/hud/thunder.png");

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_GAP = 4;   // px between an icon and the text right after it
    private static final int SEPARATOR_PAD = 3;   // px padding around the " | " separator
    private static final int HOTBAR_GAP = 6;      // px between our line and the top of the hotbar
    private static final int HOTBAR_HEIGHT = 22;  // vanilla hotbar bar is always 22px tall, see below

    private static final long UPDATE_INTERVAL_MS = 2000L; // 2 seconds

    private static long lastUpdateMs = 0L;
    private static String cachedTimeText = "--:--";
    private static Identifier cachedWeatherIcon = ICON_CLEAR;

    /*
     * Called every frame by HudElementRegistry (registered in
     * ActionBarInfoClient#onInitializeClient). The method signature is
     * fixed by the HudElement functional interface:
     *   void render(DrawContext context, RenderTickCounter tickCounter)
     * Source: net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
     */
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;

        /*
         * world/player can briefly be null (main menu, right as a world
         * unloads, etc.) - HudElementRegistry does not guarantee they are
         * loaded, so always guard against it.
         */
        if (world == null || player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= UPDATE_INTERVAL_MS) {
            lastUpdateMs = now;
            cachedTimeText = formatGameTime(world.getTimeOfDay());
            cachedWeatherIcon = pickWeatherIcon(world);
        }

        drawLine(context, client, cachedTimeText, cachedWeatherIcon);
    }

    /*
     * World#getTimeOfDay() (net.minecraft.world.World, inherited by
     * ClientWorld) returns the raw day-time counter: ticks in [0, 24000)
     * that repeat every in-game day (24000 ticks = 20 real minutes).
     *
     * Tick 0 of that counter is 6:00 (sunrise), not midnight - this is the
     * same convention the vanilla clock item's needle animation and the
     * sun/moon position use. So to get a real HH:MM we shift by +6000
     * ticks (6 hours) before converting: 1000 ticks == 1 in-game hour.
     */
    private static String formatGameTime(long timeOfDay) {
        long dayTicks = timeOfDay % 24000L;
        long shifted = (dayTicks + 6000L) % 24000L; // 0 == 00:00 after the shift
        int hours = (int) (shifted / 1000L);
        int minutes = (int) ((shifted % 1000L) * 60L / 1000L);
        return String.format("%02d:%02d", hours, minutes);
    }

    /*
     * World#isThundering() / World#isRaining() - both on
     * net.minecraft.world.World. ClientWorld keeps these in sync with the
     * server every tick via the vanilla weather packets, so this is always
     * up to date with no extra networking needed on our side.
     */
    private static Identifier pickWeatherIcon(ClientWorld world) {
        if (world.isThundering()) {
            return ICON_THUNDER;
        } else if (world.isRaining()) {
            return ICON_RAIN;
        } else {
            return ICON_CLEAR;
        }
    }

    private static void drawLine(DrawContext context, MinecraftClient client,
                                  String timeText, Identifier weatherIcon) {
        TextRenderer textRenderer = client.textRenderer;
        String separator = " | ";

        int timeTextWidth = textRenderer.getWidth(timeText);
        int sepWidth = textRenderer.getWidth(separator);

        int contentWidth = ICON_SIZE + ICON_TEXT_GAP + timeTextWidth
                + SEPARATOR_PAD + sepWidth + SEPARATOR_PAD + ICON_SIZE;

        /*
         * Window#getScaledWidth()/getScaledHeight() give the GUI-scaled
         * resolution (the same coordinate space DrawContext draws in),
         * matching InGameHud's own scaledWidth/scaledHeight fields.
         */
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = (screenWidth - contentWidth) / 2;

        /*
         * Vanilla always renders the hotbar with its top edge at
         * (scaledHeight - 22) - see
         * net.minecraft.client.gui.hud.InGameHud#renderHotbar. Anchoring
         * off that constant (instead of a hand-picked pixel offset) keeps
         * our line correctly positioned above the hotbar at any GUI scale.
         */
        int hotbarTop = screenHeight - HOTBAR_HEIGHT;
        int y = hotbarTop - HOTBAR_GAP - ICON_SIZE;
        int textY = y + (ICON_SIZE - textRenderer.fontHeight) / 2;

        /*
         * DrawContext#drawTexture(RenderPipeline, Identifier, x, y, u, v,
         * width, height, textureWidth, textureHeight) - confirmed against
         * yarn-1.21.8+build.1 javadoc:
         * https://maven.fabricmc.net/docs/yarn-1.21.8+build.1/net/minecraft/client/gui/DrawContext.html
         * RenderPipelines.GUI_TEXTURED is the standard pipeline for flat
         * unlit GUI textures (net.minecraft.client.gl.RenderPipelines).
         */
        int cursorX = x;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON_TIME,
                cursorX, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        cursorX += ICON_SIZE + ICON_TEXT_GAP;

        // DrawContext#drawTextWithShadow(TextRenderer, String, x, y, color) - confirmed via
        // https://fabricmc.net/2025/06/15/1216.html (official 1.21.6-1.21.8 changelog example)
        context.drawTextWithShadow(textRenderer, timeText, cursorX, textY, Colors.WHITE);
        cursorX += timeTextWidth + SEPARATOR_PAD;

        context.drawTextWithShadow(textRenderer, separator, cursorX, textY, Colors.WHITE);
        cursorX += sepWidth + SEPARATOR_PAD;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, weatherIcon,
                cursorX, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }
}
