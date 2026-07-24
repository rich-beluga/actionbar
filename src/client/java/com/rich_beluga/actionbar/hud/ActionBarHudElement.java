package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.data.HeldItemTooltip;
import com.rich_beluga.actionbar.data.PlayerCoordinates;
import com.rich_beluga.actionbar.data.Time;
import com.rich_beluga.actionbar.data.VanillaHud;
import com.rich_beluga.actionbar.data.Weather;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Colors;

/*
 * The single place that owns the action bar's layout: caching every
 * element's data, measuring widths, centering the whole line above the
 * hotbar, and calling each *HudElement.render(...) in the right spot.
 *
 * TimeHudElement, WeatherHudElement and CoordinatesHudElement never
 * measure or position anything themselves - they only draw at the (x, y)
 * they're handed. This is also the only class registered with
 * HudElementRegistry (see ActionBarInfoClient) - it calls the other three
 * itself instead of each of them registering its own HUD layer.
 */
public final class ActionBarHudElement {
    private ActionBarHudElement() {
        // Utility holder class, never instantiated.
    }

    /** Icon side length in pixels, shared by every icon drawn on the bar. */
    static final int ICON_SIZE = 16;
    /** Gap, in pixels, between an icon and the text next to it. */
    static final int ICON_TEXT_GAP = 4;
    /** Padding, in pixels, on each side of the " | " separator. */
    private static final int SEPARATOR_PAD = 3;
    /** Vertical gap, in pixels, between the bar and the hotbar. */
    private static final int HOTBAR_GAP = 6;
    /** Vanilla hotbar texture is always this many pixels wide. */
    private static final int HOTBAR_WIDTH = 182;
    /** Vanilla hotbar is always this many pixels tall. */
    private static final int HOTBAR_HEIGHT = 22;
    /*
     * Extra vertical space to add above the hotbar when vanilla is also
     * drawing the XP bar and/or the health/hunger/armor rows there (see
     * VanillaHud#hasExtraHud) - eyeball-tuned to roughly their combined
     * height, since we can't read vanilla's own (private) layout
     * constants for them without a mixin. Bump this up/down to taste.
     */
    private static final int EXTRA_HUD_LIFT = 19;

    private static final String SEPARATOR = " | ";

    private static final long UPDATE_INTERVAL_MS = 1000L;

    private static long lastUpdateMs = 0L;
    private static String cachedTimeText = "--:--";
    private static Weather cachedWeather = Weather.CLEAR;
    private static String cachedCoordinatesText = "X: 0 Z: 0";

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;

        if (world == null || player == null) {
            return;
        }

        /*
         * Must run every frame regardless of anything else below - it's
         * also how HeldItemTooltip notices the held item/slot changed in
         * the first place, so skipping this call while hidden would make
         * it miss the change.
         */
        boolean heldItemNameShowing = HeldItemTooltip.isShowing(player);

        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= UPDATE_INTERVAL_MS) {
            lastUpdateMs = now;
            cachedTimeText = Time.format(world.getTimeOfDay());
            cachedWeather = Weather.of(world);
            cachedCoordinatesText = PlayerCoordinates.of(player).format();
        }

        if (heldItemNameShowing) {
            // Let vanilla's own item-name overlay have the space above the hotbar to itself.
            return;
        }

        drawBar(context, client);
    }

    private static void drawBar(DrawContext context, MinecraftClient client) {
        TextRenderer textRenderer = client.textRenderer;

        int timeWidth = ICON_SIZE + ICON_TEXT_GAP + textRenderer.getWidth(cachedTimeText);
        int weatherWidth = ICON_SIZE; // icon only, no text next to it
        int coordinatesWidth = ICON_SIZE + ICON_TEXT_GAP + textRenderer.getWidth(cachedCoordinatesText);
        int separatorSpan = SEPARATOR_PAD + textRenderer.getWidth(SEPARATOR) + SEPARATOR_PAD;

        int totalWidth = timeWidth + separatorSpan + weatherWidth + separatorSpan + coordinatesWidth;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        /*
         * Center on the HOTBAR's own bounding box, not the raw screen
         * width - this stays correctly aligned even if the hotbar itself
         * isn't perfectly screen-centered (e.g. another mod repositions
         * it). Vanilla always draws the hotbar at
         * x = (scaledWidth - 182) / 2, y = scaledHeight - 22, see
         * net.minecraft.client.gui.hud.InGameHud#renderHotbar.
         */
        int hotbarX = (screenWidth - HOTBAR_WIDTH) / 2;
        int hotbarCenterX = hotbarX + HOTBAR_WIDTH / 2;
        int hotbarTop = screenHeight - HOTBAR_HEIGHT;

        /* Lift the whole bar clear of the XP bar / health / hunger / armor rows when they're showing. */
        int extraHudLift = VanillaHud.hasExtraHud(client) ? EXTRA_HUD_LIFT : 0;

        int x = hotbarCenterX - totalWidth / 2;
        int y = hotbarTop - HOTBAR_GAP - extraHudLift - ICON_SIZE;

        int cursorX = x;

        TimeHudElement.render(context, client, cachedTimeText, cursorX, y);
        cursorX += timeWidth + SEPARATOR_PAD;
        cursorX = drawSeparator(context, textRenderer, cursorX, y);
        cursorX += SEPARATOR_PAD;

        WeatherHudElement.render(context, client, cachedWeather, cursorX, y);
        cursorX += weatherWidth + SEPARATOR_PAD;
        cursorX = drawSeparator(context, textRenderer, cursorX, y);
        cursorX += SEPARATOR_PAD;

        CoordinatesHudElement.render(context, client, cachedCoordinatesText, cursorX, y);
    }

    /*
     * Draws the " | " separator at (x, y) and returns the cursor x
     * position right after it - the one place that knows how to render
     * the divider between two elements of the bar.
     */
    private static int drawSeparator(DrawContext context, TextRenderer textRenderer, int x, int y) {
        int textY = y + (ICON_SIZE - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, SEPARATOR, x, textY, Colors.WHITE);
        return x + textRenderer.getWidth(SEPARATOR);
    }
}
