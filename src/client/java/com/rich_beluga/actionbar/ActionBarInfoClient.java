package com.rich_beluga.actionbar;

import com.rich_beluga.actionbar.hud.ActionBarHudElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.util.Identifier;

public class ActionBarInfoClient implements ClientModInitializer {
    public static final String MOD_ID = "actionbar";

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(
                Identifier.of(MOD_ID, "action_bar"),
                ActionBarHudElement::render
        );
    }
}
