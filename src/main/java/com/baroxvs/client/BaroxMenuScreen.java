package com.baroxvs.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Compact wardrobe screen; each cosmetic slot can be equipped or cycled independently. */
public final class BaroxMenuScreen extends Screen {
    private final BaroxConfig config;

    public BaroxMenuScreen(BaroxConfig config) {
        super(Text.literal("BAROX VS"));
        this.config = config;
    }

    @Override
    protected void init() {
        clearChildren();
        int gap = 5;
        int columnWidth = Math.max(110, Math.min(154, (width - 28) / 2));
        int left = width / 2 - columnWidth - gap / 2;
        int right = width / 2 + gap / 2;
        int top = Math.max(32, (height - 218) / 2);
        int row = 21;

        addToggle("Kanatlar", () -> config.showWings, v -> config.showWings = v, left, top, columnWidth);
        addCycle("Kanat stili", () -> CosmeticCatalog.WINGS[config.wingStyle], () -> config.wingStyle = next(config.wingStyle, CosmeticCatalog.WINGS.length), left, top + row, columnWidth);
        addToggle("Pelerin", () -> config.showCape, v -> config.showCape = v, right, top, columnWidth);
        addCycle("Pelerin stili", () -> CosmeticCatalog.CAPES[config.capeStyle], () -> config.capeStyle = next(config.capeStyle, CosmeticCatalog.CAPES.length), right, top + row, columnWidth);
        addToggle("Takipçi hayvan", () -> config.showPet, v -> config.showPet = v, left, top + row * 2, columnWidth);
        addCycle("Hayvan seç", () -> CosmeticCatalog.PETS[config.petStyle], () -> config.petStyle = next(config.petStyle, CosmeticCatalog.PETS.length), left, top + row * 3, columnWidth);
        addToggle("Aura halkası", () -> config.showAura, v -> config.showAura = v, right, top + row * 2, columnWidth);
        addCycle("Aura stili", () -> CosmeticCatalog.AURAS[config.auraStyle], () -> config.auraStyle = next(config.auraStyle, CosmeticCatalog.AURAS.length), right, top + row * 3, columnWidth);
        addToggle("El parıltısı", () -> config.showHandGlow, v -> config.showHandGlow = v, left, top + row * 4, columnWidth);
        addToggle("Kılıç izi", () -> config.showSwordTrail, v -> config.showSwordTrail = v, right, top + row * 4, columnWidth);
        addToggle("Jump Crystal", () -> config.showJumpCrystal, v -> config.showJumpCrystal = v, left, top + row * 5, columnWidth);
        addCycle("Gökyüzü", () -> CosmeticCatalog.SKIES[config.skyStyle], () -> config.skyStyle = next(config.skyStyle, CosmeticCatalog.SKIES.length), right, top + row * 5, columnWidth);
        addCycle("Renk paleti", () -> CosmeticCatalog.PALETTES[config.palette], () -> config.palette = next(config.palette, CosmeticCatalog.PALETTES.length), left, top + row * 6, columnWidth);
        addCycle("Partikül yoğunluğu", () -> CosmeticCatalog.DENSITIES[config.particleDensity - 1], () -> config.particleDensity = config.particleDensity % 3 + 1, right, top + row * 6, columnWidth);
        addToggle("BAROX HUD", () -> config.showHud, v -> config.showHud = v, left, top + row * 7, columnWidth);
        addCycle("HUD konumu", () -> CosmeticCatalog.HUD_POSITIONS[config.hudPosition], () -> config.hudPosition = next(config.hudPosition, CosmeticCatalog.HUD_POSITIONS.length), right, top + row * 7, columnWidth);

        addDrawableChild(ButtonWidget.builder(Text.literal("Varsayılanlara dön"), button -> {
            config.showHud = true;
            config.showWings = true;
            config.showCape = false;
            config.showPet = true;
            config.showAura = true;
            config.showHandGlow = true;
            config.showSwordTrail = true;
            config.showJumpCrystal = true;
            config.showSkyMood = true;
            config.wingStyle = config.capeStyle = config.petStyle = config.auraStyle = 0;
            config.skyStyle = config.palette = config.hudPosition = 0;
            config.particleDensity = 1;
            config.save();
            init();
        }).dimensions(left, top + row * 8, columnWidth, 19).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Kaydet ve kapat"), button -> {
            config.save();
            close();
        }).dimensions(right, top + row * 8, columnWidth, 19).build());
    }

    private void addToggle(String name, BooleanSupplier getter, Consumer<Boolean> setter, int x, int y, int w) {
        ButtonWidget widget = ButtonWidget.builder(toggleLabel(name, getter.getAsBoolean()), button -> {
            boolean next = !getter.getAsBoolean();
            setter.accept(next);
            config.save();
            button.setMessage(toggleLabel(name, next));
        }).dimensions(x, y, w, 19).build();
        addDrawableChild(widget);
    }

    private void addCycle(String name, java.util.function.Supplier<String> current, Runnable cycle, int x, int y, int w) {
        ButtonWidget widget = ButtonWidget.builder(cycleLabel(name, current.get()), button -> {
            cycle.run();
            config.save();
            button.setMessage(cycleLabel(name, current.get()));
        }).dimensions(x, y, w, 19).build();
        addDrawableChild(widget);
    }

    private Text toggleLabel(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "AÇIK" : "KAPALI"));
    }

    private Text cycleLabel(String name, String current) {
        return Text.literal(name + ": " + current);
    }

    private static int next(int current, int size) {
        return (current + 1) % size;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0x33110E20);
        context.fill(width / 2 - 160, 7, width / 2 + 160, 27, 0xCC19142B);
        context.fill(width / 2 - 160, 27, width / 2 + 160, 29, CosmeticCatalog.paletteColor(config.palette));
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("BAROX VS  •  KOZMETİK GARDIROP"), width / 2, 13, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
