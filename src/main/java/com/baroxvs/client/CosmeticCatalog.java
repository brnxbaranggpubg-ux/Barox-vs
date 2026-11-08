package com.baroxvs.client;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

/** Names and vanilla particle palettes used by the cosmetic wardrobe. */
public final class CosmeticCatalog {
    private CosmeticCatalog() {}

    public static final String[] WINGS = {"Crystal", "Angel", "Demon", "Butterfly", "Void", "Dragon"};
    public static final String[] CAPES = {"Starlight", "Petal", "Soul", "Arcane"};
    public static final String[] PETS = {"Fox", "Cat", "Bunny", "Dragonling", "Wisp", "Panda"};
    public static final String[] AURAS = {"Stardust", "Solar", "Void", "Sakura"};
    public static final String[] SKIES = {"Starfield", "Aurora", "Sunset", "Void", "Clouds"};
    public static final String[] PALETTES = {"Amethyst", "Azure", "Rose", "Emerald", "Solar", "Monochrome"};
    public static final String[] DENSITIES = {"Low", "Medium", "High"};
    public static final String[] HUD_POSITIONS = {"Top left", "Top right", "Bottom left", "Bottom right"};

    public static ParticleEffect wingParticle(int style) {
        return switch (style) {
            case 1 -> ParticleTypes.HAPPY_VILLAGER;
            case 2 -> ParticleTypes.SOUL_FIRE_FLAME;
            case 3 -> ParticleTypes.CHERRY_LEAVES;
            case 4 -> ParticleTypes.PORTAL;
            case 5 -> ParticleTypes.FLAME;
            default -> ParticleTypes.END_ROD;
        };
    }

    public static ParticleEffect capeParticle(int style) {
        return switch (style) {
            case 1 -> ParticleTypes.CHERRY_LEAVES;
            case 2 -> ParticleTypes.SOUL;
            case 3 -> ParticleTypes.ENCHANT;
            default -> ParticleTypes.END_ROD;
        };
    }

    public static ParticleEffect petParticle(int style) {
        return switch (style) {
            case 1 -> ParticleTypes.HEART;
            case 2 -> ParticleTypes.HAPPY_VILLAGER;
            case 3 -> ParticleTypes.FLAME;
            case 4 -> ParticleTypes.END_ROD;
            case 5 -> ParticleTypes.CHERRY_LEAVES;
            default -> ParticleTypes.WAX_ON;
        };
    }

    public static ParticleEffect auraParticle(int style) {
        return switch (style) {
            case 1 -> ParticleTypes.FLAME;
            case 2 -> ParticleTypes.SOUL;
            case 3 -> ParticleTypes.CHERRY_LEAVES;
            default -> ParticleTypes.ENCHANT;
        };
    }

    public static ParticleEffect skyParticle(int style) {
        return switch (style) {
            case 1 -> ParticleTypes.WAX_ON;
            case 2 -> ParticleTypes.CHERRY_LEAVES;
            case 3 -> ParticleTypes.SOUL;
            case 4 -> ParticleTypes.CLOUD;
            default -> ParticleTypes.END_ROD;
        };
    }

    public static int paletteColor(int palette) {
        return switch (palette) {
            case 1 -> 0xFF6BCBFF;
            case 2 -> 0xFFFF86B7;
            case 3 -> 0xFF70E0A1;
            case 4 -> 0xFFFFC45C;
            case 5 -> 0xFFD5D8E5;
            default -> 0xFFB99AFF;
        };
    }
}
