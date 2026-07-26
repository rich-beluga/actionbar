package com.rich_beluga.actionbar.hud;

import com.rich_beluga.actionbar.data.HeldItemTooltip;
import com.rich_beluga.actionbar.data.PlayerCoordinates;
import com.rich_beluga.actionbar.data.Time;
import com.rich_beluga.actionbar.data.VanillaHud;
import com.rich_beluga.actionbar.data.Weather;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

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
 *
 * PORT NOTE (1.21.8 Yarn -> 26.2 Mojang mappings):
 * The 1.21.8 version drew directly with DrawContext#drawTexture/
 * drawTextWithShadow. As of the HUD rewrite that shipped alongside
 * unobfuscation, HudElement's callback instead hands you a
 * GuiGraphicsExtractor (which you issue draw commands to) and a
 * DeltaTracker (Yarn's RenderTickCounter, same idea, real name) - see
 * https://docs.fabricmc.net/develop/rendering/hud and the
 * "GuiGraphicsExtractor" section of https://docs.fabricmc.net/develop/rendering/gui-graphics.
 * DrawContext#drawTexture(...) -> GuiGraphicsExtractor#blit(...) (Mojang's
 * own long-standing name for that method). DrawContext#drawTextWithShadow(...)
 * -> GuiGraphicsExtractor#text(font, text, x, y, color, shadow) - note this
 * merges the two Yarn methods into one with an explicit boolean, AND as of
 * 1.21.6+ text color must be full ARGB (0xFFFFFFFF), not RGB - passing a
 * plain RGB value renders fully transparent text.
 */
public final class ActionBarHudElement {
    private ActionBarHudElement() {
        // Utility holder class, never instantiated.
    }

    /** Icon side length in pixels, shared by every icon drawn on the bar. */
    static final int ICON_SIZE = 16;
    /** Gap, in pixels, between a segment's icon and its own text. */
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
     * drawing the XP bar and the health/hunger/armor rows there (see
     * VanillaHud#hasExtraHud) - eyeball-tuned, since we can't read
     * vanilla's own (private) layout constants without a mixin.
     */
    private static final int EXTRA_HUD_LIFT = 22;
    /* Same as above, but for when the armor row specifically isn't showing (VanillaHud#hasArmor is false). */
    private static final int EXTRA_HUD_LIFT_NO_ARMOR = 13;

    private static final String SEPARATOR = " | ";
    /* Full ARGB white - see the PORT NOTE above about 1.21.6+'s ARGB text color requirement. */
    private static final int WHITE = 0xFFFFFFFF;

    private static final long UPDATE_INTERVAL_MS = 100L;

    private static long lastUpdateMs = 0L;
    private static String cachedTimeText = "--:--";
    private static Weather cachedWeather = Weather.CLEAR;
    private static String cachedCoordinatesText = "X: 0 Z: 0";

    /*
     * Registered from ActionBarInfoClient via HudElementRegistry. Signature
     * fixed by the HudElement functional interface (see the PORT NOTE above).
     */
    public static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;

        /* level/player can briefly be null (main menu, world just unloaded, ...). */
        if (level == null || player == null) {
            return;
        }

        /*
         * Must run every frame regardless of anything else below - it's
         * also how HeldItemTooltip notices the held item changed in the
         * first place, so skipping this call while hidden would make it
         * miss the change.
         */
        boolean heldItemNameShowing = HeldItemTooltip.isShowing(player);

        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= UPDATE_INTERVAL_MS) {
            lastUpdateMs = now;
            cachedTimeText = Time.format(Time.currentDayTicks(level));
            cachedWeather = Weather.of(level);
            cachedCoordinatesText = PlayerCoordinates.of(player).format();
        }

        if (heldItemNameShowing) {
            // Let vanilla's own item-name overlay have the space above the hotbar to itself.
            return;
        }

        drawBar(graphics, minecraft);
    }

    private static void drawBar(GuiGraphicsExtractor graphics, Minecraft minecraft) {
        Font font = minecraft.font;

        int timeWidth = ICON_SIZE + ICON_TEXT_GAP + font.width(cachedTimeText);
        int weatherWidth = ICON_SIZE; // icon only, no text next to it
        int coordinatesWidth = ICON_SIZE + ICON_TEXT_GAP + font.width(cachedCoordinatesText);
        int separatorSpan = SEPARATOR_PAD + font.width(SEPARATOR) + SEPARATOR_PAD;

        int totalWidth = timeWidth + separatorSpan + weatherWidth + separatorSpan + coordinatesWidth;

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        /*
         * Center on the HOTBAR's own bounding box, not the raw screen
         * width - vanilla always draws the hotbar at
         * x = (scaledWidth - 182) / 2, y = scaledHeight - 22.
         */
        int hotbarX = (screenWidth - HOTBAR_WIDTH) / 2;
        int hotbarCenterX = hotbarX + HOTBAR_WIDTH / 2;
        int hotbarTop = screenHeight - HOTBAR_HEIGHT;

        /*
         * Three tiers, cheapest check first:
         *  - not survival-like (creative/spectator) -> no extra HUD at all, lift = 0
         *  - survival-like, no armor equipped        -> XP + health/hunger only
         *  - survival-like, armor equipped            -> XP + health/hunger + armor row
         */
        int extraHudLift = 0;
        if (VanillaHud.hasExtraHud(minecraft)) {
            extraHudLift = VanillaHud.hasArmor(minecraft) ? EXTRA_HUD_LIFT : EXTRA_HUD_LIFT_NO_ARMOR;
        }

        int x = hotbarCenterX - totalWidth / 2;
        int y = hotbarTop - HOTBAR_GAP - extraHudLift - ICON_SIZE;

        int cursorX = x;

        TimeHudElement.render(graphics, font, cachedTimeText, cursorX, y);
        cursorX += timeWidth + SEPARATOR_PAD;
        cursorX = drawSeparator(graphics, font, cursorX, y);
        cursorX += SEPARATOR_PAD;

        WeatherHudElement.render(graphics, cachedWeather, cursorX, y);
        cursorX += weatherWidth + SEPARATOR_PAD;
        cursorX = drawSeparator(graphics, font, cursorX, y);
        cursorX += SEPARATOR_PAD;

        CoordinatesHudElement.render(graphics, font, cachedCoordinatesText, cursorX, y);
    }

    /**
     * Draws the " | " separator at (x, y) and returns the cursor x
     * position right after it - the one place that knows how to render
     * the divider between two elements of the bar.
     */
    private static int drawSeparator(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        int textY = y + (ICON_SIZE - font.lineHeight) / 2;
        graphics.text(font, SEPARATOR, x, textY, WHITE, true);
        return x + font.width(SEPARATOR);
    }
}
