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
            if (hudEnabled && CONFIG.showHud && CONFIG.moduleEnabled("status_bar", true)
                    && client.player != null) {
                renderHud(drawContext, client);
            }
        });
    }

    private static void renderHud(DrawContext context, MinecraftClient client) {
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int w = 164;
        int x = switch (CONFIG.hudPosition) {
            case 1, 3 -> sw - w - 8;
            default -> 8;
        };
        int y = switch (CONFIG.hudPosition) {
            case 2, 3 -> sh - 8 - 16 * 7;
            default -> 8;
        };
        int accent = CosmeticCatalog.paletteColor(CONFIG.palette);
        context.fill(x - 4, y - 4, x + w, y + 16 * 7, 0xB8121020);
        context.fill(x - 4, y - 4, x + w, y - 2, accent);
        int line = 0;
        context.drawTextWithShadow(client.textRenderer,
                Text.literal("BAROX VS  •  v002"), x, y + line++ * 15, 0xFFFFFFFF);

        if (CONFIG.moduleEnabled("fps", true)) {
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("FPS: " + client.getCurrentFps()), x, y + line++ * 15, 0xFFE1D8FF);
        }
        if (CONFIG.moduleEnabled("coordinates", true)) {
            var player = client.player;
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format(java.util.Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                            player.getX(), player.getY(), player.getZ())),
                    x, y + line++ * 15, 0xFFE1D8FF);
        }
        if (CONFIG.moduleEnabled("direction", true)) {
            String[] directions = {"South", "West", "North", "East"};
            int index = Math.floorMod((int) Math.floor(client.player.getYaw() / 90.0 + 0.5), 4);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("Facing: " + directions[index]), x, y + line++ * 15, 0xFFB9C7FF);
        }
        if (CONFIG.moduleEnabled("biome", false) && client.world != null) {
            String biome = client.world.getBiome(client.player.getBlockPos()).getKey()
                    .map(key -> key.getValue().getPath()).orElse("unknown");
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("Biome: " + biome), x, y + line++ * 15, 0xFFB9C7FF);
        }
        if (CONFIG.moduleEnabled("ping", false) && client.getNetworkHandler() != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) {
                context.drawTextWithShadow(client.textRenderer,
                        Text.literal("Ping: " + entry.getLatency() + " ms"), x, y + line++ * 15, 0xFFB9C7FF);
            }
        }
        if (CONFIG.moduleEnabled("local_clock", false)) {
            String now = java.time.LocalTime.now().withSecond(0).toString();
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("Local: " + now), x, y + line++ * 15, 0xFFB9C7FF);
        }
    }
}
