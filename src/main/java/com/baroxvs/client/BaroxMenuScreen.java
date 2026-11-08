package com.baroxvs.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * BAROX VS v002 ClickGUI.
 * Responsive purple multi-panel layout inspired by the supplied reference.
 * All switches are local cosmetics, HUD or presentation preferences.
 */
public final class BaroxMenuScreen extends Screen {
    private static final int PURPLE = 0xFFB44CFF;
    private static final int PANEL = 0xE8171027;
    private static final int PANEL_ALT = 0xD91E1633;
    private static final int TEXT = 0xFFF4EDFF;
    private static final int MUTED = 0xFFB6A9C9;

    private final BaroxConfig config;
    private final List<HitBox> hitBoxes = new ArrayList<>();
    private TextFieldWidget search;
    private String page = "Ana Sayfa";
    private double scroll;
    private int contentBottom;

    private record Module(String id, String name, String description, boolean enabled) {}
    private record Category(String name, String icon, List<Module> modules) {}
    private record HitBox(int x1, int y1, int x2, int y2, Runnable action) {
        boolean contains(double x, double y) { return x >= x1 && x <= x2 && y >= y1 && y <= y2; }
    }

    public BaroxMenuScreen(BaroxConfig config) {
        super(Text.literal("BAROX VS"));
        this.config = config;
    }

    private List<Category> categories() {
        return List.of(
            category("Combat", "✦",
                m("aura","Aura Ring","Local decorative ring",true),
                m("sword_trail","Sword Sparkle","Particles on your held sword",true),
                m("hand_glow","Hand Glow","Small hand-side glow",true),
                m("hit_sparkles","Hit Sparkles","Decorative particles around your own player",false)),
            category("Movement", "➤",
                m("footsteps","Footstep Trail","Cosmetic particles while walking",true),
                m("jump_crystal","Jump Crystal","Particle ring while airborne",true),
                m("landing_burst","Landing Burst","Small landing sparkle effect",false),
                m("orbit_stars","Orbit Stars","Stars orbit around your player",true)),
            category("Visual", "◈",
                m("wings","Wings","Particle wing styles",true),
                m("cape","Cape Trail","Particle cape trail",false),
                m("pet","Follower Pet","Local particle companion",true),
                m("halo","Halo","Floating halo above your head",true),
                m("sky_mood","Sky Mood","Ambient particles (not a skybox)",true),
                m("sparkle_trail","Sparkle Trail","Soft star trail around you",false),
                m("heart_trail","Heart Trail","Floating heart particles",false),
                m("bubble_trail","Bubble Trail","Bubble particles around you",false),
                m("crown_sparkles","Crown Sparkles","Tiny crown-like sparkle arc",false),
                m("shoulder_wisp","Shoulder Wisp","A small shoulder-side wisp",false)),
            category("Player", "♟",
                m("profile_card","Profile Card","Show your local player profile",true),
                m("session_timer","Session Timer","Time since this world was opened",false),
                m("direction","Direction","Compass direction in the HUD",true),
                m("biome","Biome Label","Current biome name in the HUD",false),
                m("local_clock","Local Clock","Local device time display",false)),
            category("HUD", "▣",
                m("fps","FPS Counter","Frames per second",true),
                m("coordinates","Coordinates","Your current XYZ coordinates",true),
                m("ping","Ping","Connection latency when available",false),
                m("armor_hud","Armor HUD","Equipped armor durability summary",false),
                m("potion_hud","Potion Timers","Active effect names and time",false),
                m("keystrokes","Keystrokes","Show movement key states",false),
                m("crosshair_dot","Crosshair Dot","Small center dot overlay",false),
                m("status_bar","Status Bar","Compact BAROX status panel",true),
                m("clock_hud","Clock HUD","World day-time readout",false)),
            category("Themes", "✿",
                m("purple_theme","Purple Dream","Purple glass UI theme",true),
                m("animated_ui","Animated Panels","Subtle panel animation",true),
                m("soft_glow","Soft Glow","Accent glow on active controls",true),
                m("compact_ui","Compact UI","Denser module cards",false),
                m("sound_ui","UI Sounds","Reserved for future custom UI sounds",false)),
            category("Render", "▧",
                m("low_fire","Low Fire","Lower first-person fire overlay",false),
                m("minimal_hud","Minimal HUD","Hide selected vanilla HUD elements",false),
                m("item_labels","Item Labels","Show held item name briefly",false),
                m("reduced_particles","Reduced Particles","Reduce BAROX cosmetic particle count",false),
                m("clean_screenshot","Screenshot Mode","Temporarily hide BAROX overlays",false)),
            category("Fun", "★",
                m("rainbow_name","Rainbow Name","Animated local name accent",false),
                m("emote_wheel","Emote Wheel","Cosmetic emote selector shell",false),
                m("confetti","Confetti","Toggle celebratory local particles",false),
                m("sparkle_cursor","Sparkle Cursor","Decorative menu pointer accent",false))
        );
    }

    private Module m(String id, String name, String description, boolean enabled) {
        return new Module(id, name, description, enabled);
    }
    private Category category(String name, String icon, Module... modules) {
        return new Category(name, icon, List.of(modules));
    }

    @Override
    protected void init() {
        clearChildren();
        int mainX = Math.min(190, Math.max(12, width / 5));
        int searchX = mainX + 10;
        search = new TextFieldWidget(textRenderer, searchX, 79, Math.max(100, width - mainX - 30), 20, Text.literal("Ara"));
        search.setMaxLength(48);
        search.setPlaceholder(Text.literal("Modül ara..."));
        addDrawableChild(search);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        hitBoxes.clear();
        int accent = CosmeticCatalog.paletteColor(config.palette);
        ctx.fill(0, 0, width, height, 0x55100720);
        // Top navigation glass bar
        roundedPanel(ctx, 12, 12, width - 12, 61, 0xE8100A1C, accent);
        ctx.drawTextWithShadow(textRenderer, Text.literal("♛ BAROX"), 25, 25, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("VS"), 86, 25, accent);
        ctx.drawTextWithShadow(textRenderer, Text.literal("v002  •  1.21.4"), 25, 42, MUTED);
        String[] tabs = {"Ana Sayfa", "Modüller", "Ayarlar", "Profil", "İstatistikler", "Hakkında"};
        int tabStart = Math.max(150, width / 4);
        int tabW = Math.max(66, (width - tabStart - 22) / tabs.length);
        for (int i = 0; i < tabs.length; i++) {
            int x = tabStart + i * tabW;
            boolean selected = page.equals(tabs[i]) || (page.equals("Tümü") && i == 0);
            ctx.fill(x, 21, x + tabW - 4, 52, selected ? 0xFF7010C9 : 0x552B1C43);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(tabs[i]), x + (tabW - 4) / 2, 32,
                    selected ? 0xFFFFFFFF : MUTED);
            final String target = tabs[i];
            hitBoxes.add(new HitBox(x, 21, x + tabW - 4, 52, () -> { page = target.equals("Modüller") ? "Tümü" : target; scroll = 0; }));
        }

        int sideX = 14, sideY = 82, sideW = Math.min(158, Math.max(118, width / 6));
        int sideH = height - 98;
        roundedPanel(ctx, sideX, sideY, sideX + sideW, sideY + sideH, PANEL, accent);
        ctx.drawTextWithShadow(textRenderer, Text.literal("BARAN3120  ♛"), sideX + 12, sideY + 13, TEXT);
        ctx.drawTextWithShadow(textRenderer, Text.literal("◆ Premium • Local"), sideX + 12, sideY + 28, accent);
        String[] nav = {"Ana Sayfa","Tümü","Visual","HUD","Themes","Render","Fun","Ayarlar"};
        int navY = sideY + 52;
        for (String item : nav) {
            int y = navY;
            boolean active = page.equals(item) || (item.equals("Ana Sayfa") && page.equals("Ana Sayfa"));
            ctx.fill(sideX + 7, y, sideX + sideW - 7, y + 25, active ? 0xFF6500B8 : 0x00201030);
            ctx.drawTextWithShadow(textRenderer, Text.literal(item), sideX + 16, y + 8, active ? TEXT : MUTED);
            hitBoxes.add(new HitBox(sideX + 7, y, sideX + sideW - 7, y + 25, () -> { page = item; scroll = 0; }));
            navY += 31;
        }
        ctx.drawTextWithShadow(textRenderer, Text.literal("Fabric 1.21.4"), sideX + 12, sideY + sideH - 30, MUTED);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Client-side visuals"), sideX + 12, sideY + sideH - 18, 0xFF8EE6B5);

        int rightW = width >= 900 ? 178 : 0;
        int mainX = sideX + sideW + 12;
        int rightX = width - rightW - 14;
        int mainRight = rightW > 0 ? rightX - 10 : width - 14;
        int mainW = mainRight - mainX;
        int top = 108;
        int bottom = height - 16;
        contentBottom = bottom;
        // Search field backdrop and title
        ctx.drawTextWithShadow(textRenderer, Text.literal(page.equals("Ana Sayfa") ? "Kontrol Merkezi" : page + " Modülleri"),
                mainX + 3, 66, TEXT);
        ctx.fill(mainX, 76, mainRight, 102, 0xAA160F25);
        search.setX(mainX + 8);
        search.setY(79);
        search.setWidth(Math.max(90, mainW - 16));

        if (page.equals("Ayarlar")) {
            renderSettings(ctx, mainX, top, mainW, accent);
        } else if (page.equals("Profil") || page.equals("İstatistikler") || page.equals("Hakkında")) {
            renderInfo(ctx, mainX, top, mainW, page, accent);
        } else {
            renderModuleCards(ctx, mainX, top, mainW, bottom, accent);
        }

        if (rightW > 0) renderQuickPanel(ctx, rightX, top, rightW, bottom, accent);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderModuleCards(DrawContext ctx, int x, int y, int w, int bottom, int accent) {
        String query = search.getText().trim().toLowerCase(Locale.ROOT);
        List<Category> shown = new ArrayList<>();
        for (Category c : categories()) {
            if (!page.equals("Ana Sayfa") && !page.equals("Tümü") && !page.equals("Modüller") && !c.name.equals(page)) continue;
            List<Module> filtered = c.modules.stream().filter(m ->
                    query.isEmpty() || m.name.toLowerCase(Locale.ROOT).contains(query)
                            || m.description.toLowerCase(Locale.ROOT).contains(query)).toList();
            if (!filtered.isEmpty()) shown.add(new Category(c.name, c.icon, filtered));
        }
        int cols = w >= 850 ? 3 : w >= 520 ? 2 : 1;
        int gap = 8;
        int cardW = (w - gap * (cols - 1)) / cols;
        int cardH = page.equals("Ana Sayfa") && query.isEmpty() ? 190 : 270;
        int row = 0, col = 0;
        for (Category c : shown) {
            int cx = x + col * (cardW + gap);
            int cy = y + row * (cardH + gap) - (int)scroll;
            if (cy + cardH > y - 4 && cy < bottom) {
                roundedPanel(ctx, cx, cy, cx + cardW, cy + cardH, PANEL, accent);
                ctx.drawTextWithShadow(textRenderer, Text.literal(c.icon + "  " + c.name), cx + 10, cy + 9, TEXT);
                ctx.drawTextWithShadow(textRenderer, Text.literal(c.modules.size() + " modules"), cx + cardW - 72, cy + 9, MUTED);
                int visible = page.equals("Ana Sayfa") && query.isEmpty() ? Math.min(6, c.modules.size()) : c.modules.size();
                for (int i = 0; i < visible; i++) {
                    Module mod = c.modules.get(i);
                    int my = cy + 29 + i * 22;
                    if (my + 19 > cy + cardH - 5) break;
                    boolean enabled = config.moduleEnabled(mod.id, mod.enabled);
                    ctx.drawTextWithShadow(textRenderer, Text.literal("✿ " + mod.name), cx + 10, my + 5, enabled ? TEXT : MUTED);
                    drawSwitch(ctx, cx + cardW - 34, my + 2, enabled, accent);
                    final String id = mod.id;
                    hitBoxes.add(new HitBox(cx + 5, my, cx + cardW - 5, my + 20, () -> toggle(id, mod.enabled)));
                }
                if (visible < c.modules.size()) {
                    int moreY = cy + cardH - 20;
                    ctx.drawTextWithShadow(textRenderer, Text.literal("+" + (c.modules.size() - visible) + " daha • aç"), cx + 10, moreY, accent);
                    hitBoxes.add(new HitBox(cx + 4, moreY - 3, cx + cardW - 4, moreY + 13, () -> { page = c.name; scroll = 0; }));
                }
                hitBoxes.add(new HitBox(cx, cy, cx + cardW, cy + 24, () -> { page = c.name; scroll = 0; }));
            }
            col++;
            if (col >= cols) { col = 0; row++; }
        }
    }

    private void renderQuickPanel(DrawContext ctx, int x, int y, int w, int bottom, int accent) {
        roundedPanel(ctx, x, y, x + w, Math.min(bottom, y + 240), PANEL, accent);
        ctx.drawTextWithShadow(textRenderer, Text.literal("⚙ Quick Settings"), x + 10, y + 10, TEXT);
        String[][] quick = {
            {"FPS Counter","fps"}, {"Coordinates","coordinates"}, {"Direction","direction"},
            {"Orbit Stars","orbit_stars"}, {"Halo","halo"}, {"Sparkle Trail","sparkle_trail"}
        };
        int qy = y + 34;
        for (String[] item : quick) {
            boolean state = config.moduleEnabled(item[1], defaultFor(item[1]));
            ctx.drawTextWithShadow(textRenderer, Text.literal(item[0]), x + 10, qy + 4, MUTED);
            drawSwitch(ctx, x + w - 34, qy, state, accent);
            hitBoxes.add(new HitBox(x + 5, qy - 2, x + w - 5, qy + 19, () -> toggle(item[1], defaultFor(item[1]))));
            qy += 27;
        }
        ctx.fill(x + 8, qy + 4, x + w - 8, qy + 5, 0x664C3866);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Theme"), x + 10, qy + 14, TEXT);
        ctx.drawTextWithShadow(textRenderer, Text.literal(CosmeticCatalog.PALETTES[config.palette]), x + 10, qy + 29, accent);
        hitBoxes.add(new HitBox(x + 8, qy + 8, x + w - 8, qy + 43, () -> { config.palette = (config.palette + 1) % CosmeticCatalog.PALETTES.length; config.save(); }));
        int saveY = Math.min(bottom - 34, qy + 52);
        ctx.fill(x + 8, saveY, x + w - 8, saveY + 25, 0xFF7200C8);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Modülleri Kaydet"), x + w / 2, saveY + 8, TEXT);
        hitBoxes.add(new HitBox(x + 8, saveY, x + w - 8, saveY + 25, config::save));
    }

    private void renderSettings(DrawContext ctx, int x, int y, int w, int accent) {
        roundedPanel(ctx, x, y, x + w, Math.min(height - 16, y + 310), PANEL, accent);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Görünüm ve performans"), x + 14, y + 14, TEXT);
        String[] labels = {"Palette","Particle density","HUD position","Compact cards"};
        String[] values = {CosmeticCatalog.PALETTES[config.palette], CosmeticCatalog.DENSITIES[config.particleDensity - 1],
                CosmeticCatalog.HUD_POSITIONS[config.hudPosition], config.moduleEnabled("compact_ui", false) ? "On" : "Off"};
        for (int i = 0; i < labels.length; i++) {
            int yy = y + 46 + i * 38;
            ctx.drawTextWithShadow(textRenderer, Text.literal(labels[i]), x + 14, yy, MUTED);
            ctx.drawTextWithShadow(textRenderer, Text.literal(values[i] + "  ›"), x + w - 150, yy, accent);
            final int index = i;
            hitBoxes.add(new HitBox(x + 8, yy - 5, x + w - 8, yy + 20, () -> {
                switch(index) {
                    case 0 -> config.palette = (config.palette + 1) % CosmeticCatalog.PALETTES.length;
                    case 1 -> config.particleDensity = config.particleDensity % 3 + 1;
                    case 2 -> config.hudPosition = (config.hudPosition + 1) % 4;
                    case 3 -> toggle("compact_ui", false);
                }
                config.save();
            }));
        }
    }

    private void renderInfo(DrawContext ctx, int x, int y, int w, String section, int accent) {
        roundedPanel(ctx, x, y, x + w, y + 210, PANEL, accent);
        String[] lines = switch(section) {
            case "Profil" -> new String[]{"BARAN3120  ♛", "BAROX VS Premium-style local profile", "Cosmetics and settings are saved on this device."};
            case "İstatistikler" -> new String[]{"BAROX VS CLIENT", "Local cosmetic modules: " + categories().stream().mapToInt(c -> c.modules.size()).sum(),
                    "Active switches: " + (config.modules == null ? 0 : config.modules.values().stream().filter(Boolean::booleanValue).count())};
            default -> new String[]{"BAROX VS v002", "Minecraft Java 1.21.4 • Fabric • Java 21",
                    "Client-side visuals, HUD and cosmetic customization only."};
        };
        for (int i = 0; i < lines.length; i++) ctx.drawTextWithShadow(textRenderer, Text.literal(lines[i]), x + 16, y + 20 + i * 24, i == 0 ? accent : TEXT);
    }

    private void toggle(String id, boolean fallback) {
        config.setModuleEnabled(id, !config.moduleEnabled(id, fallback));
        applyLegacyToggle(id, config.moduleEnabled(id, fallback));
        config.save();
    }

    private void applyLegacyToggle(String id, boolean state) {
        switch(id) {
            case "wings" -> config.showWings = state;
            case "cape" -> config.showCape = state;
            case "pet" -> config.showPet = state;
            case "aura" -> config.showAura = state;
            case "hand_glow" -> config.showHandGlow = state;
            case "sword_trail" -> config.showSwordTrail = state;
            case "jump_crystal" -> config.showJumpCrystal = state;
            case "sky_mood" -> config.showSkyMood = state;
            case "orbit_stars" -> config.showOrbitStars = state;
            case "footsteps" -> config.showFootsteps = state;
            case "halo" -> config.showHalo = state;
        }
    }

    private boolean defaultFor(String id) {
        return switch(id) {
            case "fps","coordinates","direction","orbit_stars","halo","status_bar","wings","pet","aura","footsteps","jump_crystal","sky_mood" -> true;
            default -> false;
        };
    }

    private void drawSwitch(DrawContext ctx, int x, int y, boolean enabled, int accent) {
        ctx.fill(x, y, x + 25, y + 13, enabled ? accent : 0xFF51465D);
        ctx.fill(x + (enabled ? 13 : 1), y + 1, x + (enabled ? 24 : 12), y + 12, 0xFFFFFFFF);
    }

    private void roundedPanel(DrawContext ctx, int x1, int y1, int x2, int y2, int color, int accent) {
        ctx.fill(x1, y1, x2, y2, color);
        ctx.fill(x1, y1, x2, y1 + 1, 0x885F3B83);
        ctx.fill(x1, y1, x1 + 1, y2, 0x885F3B83);
        ctx.fill(x1 + 1, y2 - 1, x2 - 1, y2, 0x663A2750);
        ctx.fill(x2 - 1, y1 + 1, x2, y2 - 1, 0x663A2750);
        ctx.fill(x1 + 9, y1 + 25, x1 + 12, y1 + 39, accent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        for (int i = hitBoxes.size() - 1; i >= 0; i--) {
            HitBox hit = hitBoxes.get(i);
            if (hit.contains(mouseX, mouseY)) { hit.action.run(); return true; }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, Math.min(1400, scroll - verticalAmount * 28));
        return true;
    }

    @Override
    public void close() {
        config.save();
        super.close();
    }
}
