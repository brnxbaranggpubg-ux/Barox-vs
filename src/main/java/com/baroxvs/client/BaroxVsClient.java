package com.baroxvs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class BaroxVsClient implements ClientModInitializer {
    private static final BaroxConfig CONFIG = BaroxConfig.load();
    private static KeyBinding openMenuKey;
    private static KeyBinding toggleHudKey;
    private static boolean hudEnabled = true;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.baroxvs.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.category.baroxvs"));
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.baroxvs.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT,
                "key.category.baroxvs"));

        new CosmeticEngine(CONFIG).register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new BaroxMenuScreen(CONFIG));
            }
            while (toggleHudKey.wasPressed()) hudEnabled = !hudEnabled;
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (hudEnabled && CONFIG.showHud && client.player != null) renderHud(drawContext, client);
        });
    }

    private static void renderHud(DrawContext context, MinecraftClient client) {
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int w = 134;
        int h = 48;
        int x = switch (CONFIG.hudPosition) {
            case 1, 3 -> sw - w - 8;
            default -> 8;
        };
        int y = switch (CONFIG.hudPosition) {
            case 2, 3 -> sh - h - 8;
            default -> 8;
        };
        int accent = CosmeticCatalog.paletteColor(CONFIG.palette);
        context.fill(x - 4, y - 4, x + w, y + h, 0xB8121020);
        context.fill(x - 4, y - 4, x + w, y - 2, accent);
        context.drawTextWithShadow(client.textRenderer, Text.literal("BAROX VS"), x, y, 0xFFFFFFFF);
        context.drawTextWithShadow(client.textRenderer,
                Text.literal((CONFIG.showWings ? "✦ " + CosmeticCatalog.WINGS[CONFIG.wingStyle] : "Wings off")
                        + "  •  " + (CONFIG.showPet ? CosmeticCatalog.PETS[CONFIG.petStyle] : "No pet")),
                x, y + 12, 0xFFE1D8FF);
        context.drawTextWithShadow(client.textRenderer,
                Text.literal("Sky: " + (CONFIG.showSkyMood ? CosmeticCatalog.SKIES[CONFIG.skyStyle] : "Vanilla")),
                x, y + 24, 0xFFB9C7FF);
        context.drawTextWithShadow(client.textRenderer,
                Text.literal("Right Shift  •  Wardrobe"), x, y + 36, 0xFFA9A1B8);
    }
}
