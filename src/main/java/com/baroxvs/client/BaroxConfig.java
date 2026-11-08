package com.baroxvs.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistent client-side wardrobe settings. */
public final class BaroxConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("baroxvs.json");

    public boolean showHud = true;
    public boolean showWings = true;
    public boolean showCape = false;
    public boolean showPet = true;
    public boolean showAura = true;
    public boolean showHandGlow = true;
    public boolean showSwordTrail = true;
    public boolean showJumpCrystal = true;
    public boolean showSkyMood = true;
    public boolean showOrbitStars = true;
    public boolean showFootsteps = true;
    public boolean showHalo = true;

    public int wingStyle = 0;
    public int capeStyle = 0;
    public int petStyle = 0;
    public int auraStyle = 0;
    public int skyStyle = 0;
    public int palette = 0;
    public int particleDensity = 1;
    public int hudPosition = 0;

    public static BaroxConfig load() {
        try {
            if (Files.exists(FILE)) {
                BaroxConfig loaded = GSON.fromJson(Files.readString(FILE), BaroxConfig.class);
                if (loaded != null) {
                    loaded.sanitize();
                    return loaded;
                }
            }
        } catch (Exception exception) {
            System.err.println("[BAROX VS] Config load failed; defaults will be used: " + exception.getMessage());
        }
        BaroxConfig config = new BaroxConfig();
        config.save();
        return config;
    }

    public void sanitize() {
        wingStyle = clamp(wingStyle, 0, 9);
        capeStyle = clamp(capeStyle, 0, 3);
        petStyle = clamp(petStyle, 0, 7);
        auraStyle = clamp(auraStyle, 0, 5);
        skyStyle = clamp(skyStyle, 0, 4);
        palette = clamp(palette, 0, 5);
        particleDensity = clamp(particleDensity, 1, 3);
        hudPosition = clamp(hudPosition, 0, 3);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public void save() {
        sanitize();
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException exception) {
            System.err.println("[BAROX VS] Could not save config: " + exception.getMessage());
        }
    }
}
