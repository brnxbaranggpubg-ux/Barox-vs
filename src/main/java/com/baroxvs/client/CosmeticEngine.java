package com.baroxvs.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Emits local-only cosmetic particles. No packets, combat automation, entity scanning,
 * or server-side gameplay changes are performed here.
 */
public final class CosmeticEngine {
    private final BaroxConfig config;
    private int tick;

    public CosmeticEngine(BaroxConfig config) {
        this.config = config;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (client.world == null || client.player == null || client.player.isSpectator()) return;
        PlayerEntity player = client.player;
        tick++;
        int stride = config.particleDensity == 1 ? 4 : config.particleDensity == 2 ? 2 : 1;
        if (tick % stride != 0) return;

        float yaw = (float) Math.toRadians(player.getYaw());
        double rightX = Math.cos(yaw);
        double rightZ = Math.sin(yaw);
        double backX = Math.sin(yaw);
        double backZ = -Math.cos(yaw);

        if (config.moduleEnabled("wings", config.showWings)) emitWings(client, player, rightX, rightZ, backX, backZ);
        if (config.moduleEnabled("cape", config.showCape) && tick % 2 == 0) emitCape(client, player, rightX, rightZ, backX, backZ);
        if (config.moduleEnabled("pet", config.showPet) && tick % 2 == 0) emitPet(client, player, rightX, rightZ, backX, backZ);
        if (config.moduleEnabled("aura", config.showAura)) emitAura(client, player);
        if (config.moduleEnabled("hand_glow", config.showHandGlow)) emitHand(client, player, rightX, rightZ, backX, backZ, ParticleTypes.END_ROD);
        if (config.moduleEnabled("sword_trail", config.showSwordTrail) && player.getMainHandStack().getItem() instanceof SwordItem) {
            emitHand(client, player, rightX, rightZ, backX, backZ, CosmeticCatalog.wingParticle(config.wingStyle));
        }
        if (config.moduleEnabled("jump_crystal", config.showJumpCrystal) && !player.isOnGround()) emitJumpCrystal(client, player);
        if (config.moduleEnabled("sky_mood", config.showSkyMood) && tick % 5 == 0) emitSkyMood(client, player);
        if (config.moduleEnabled("orbit_stars", config.showOrbitStars)) emitOrbitStars(client, player);
        if (config.moduleEnabled("footsteps", config.showFootsteps) && player.isOnGround()
                && player.getVelocity().horizontalLengthSquared() > 0.0025) emitFootsteps(client, player);
        if (config.moduleEnabled("halo", config.showHalo)) emitHalo(client, player);
        if (config.moduleEnabled("sparkle_trail", false) && tick % 2 == 0)
            spawn(client, ParticleTypes.END_ROD, player.getX(), player.getY() + 0.55, player.getZ(), 0, 0.018, 0);
        if (config.moduleEnabled("heart_trail", false) && tick % 4 == 0)
            spawn(client, ParticleTypes.HEART, player.getX(), player.getY() + 1.05, player.getZ(), 0, 0.025, 0);
        if (config.moduleEnabled("bubble_trail", false) && tick % 3 == 0)
            spawn(client, ParticleTypes.BUBBLE, player.getX(), player.getY() + 0.25, player.getZ(), 0, 0.035, 0);
        if (config.moduleEnabled("crown_sparkles", false)) emitCrown(client, player);
        if (config.moduleEnabled("shoulder_wisp", false)) emitShoulderWisp(client, player, rightX, rightZ);
    }

    private void emitWings(MinecraftClient client, PlayerEntity p,
                           double rx, double rz, double bx, double bz) {
        ParticleEffect particle = CosmeticCatalog.wingParticle(config.wingStyle);
        for (int side : new int[]{-1, 1}) {
            for (int feather = 0; feather < 7; feather++) {
                double span = 0.22 + feather * 0.105;
                double lift = 1.20 + Math.sin(feather * 0.42) * 0.30 - feather * 0.025;
                for (int segment = 0; segment < 3; segment++) {
                    double t = segment / 2.0;
                    double lateral = side * (0.18 + (span - 0.18) * t);
                    double y = p.getY() + lift - t * (0.12 + feather * 0.012);
                    double depth = 0.18 + feather * 0.012;
                    spawn(client, particle, p.getX() + rx * lateral + bx * depth, y,
                            p.getZ() + rz * lateral + bz * depth, 0, 0.006, 0);
                }
            }
        }
    }

    private void emitCape(MinecraftClient client, PlayerEntity p, double rx, double rz, double bx, double bz) {
        ParticleEffect particle = CosmeticCatalog.capeParticle(config.capeStyle);
        for (int row = 0; row < 4; row++) {
            for (int col = -1; col <= 1; col++) {
                double side = col * 0.23;
                double depth = 0.24 + row * 0.095;
                spawn(client, particle, p.getX() + rx * side + bx * depth,
                        p.getY() + 1.18 - row * 0.22, p.getZ() + rz * side + bz * depth, 0, 0.012, 0);
            }
        }
    }

    private void emitPet(MinecraftClient client, PlayerEntity p, double rx, double rz, double bx, double bz) {
        ParticleEffect pet = CosmeticCatalog.petParticle(config.petStyle);
        double phase = tick * 0.12;
        double side = 0.78 + Math.sin(phase) * 0.12;
        double back = 0.10 + Math.cos(phase) * 0.10;
        double bob = 0.78 + Math.sin(phase * 1.7) * 0.12;
        double x = p.getX() + rx * side + bx * back;
        double y = p.getY() + bob;
        double z = p.getZ() + rz * side + bz * back;
        spawn(client, pet, x, y, z, 0, 0.01, 0);
        spawn(client, pet, x + rx * 0.08, y + 0.10, z + rz * 0.08, 0, 0.01, 0);
        spawn(client, pet, x - rx * 0.11, y + 0.16, z - rz * 0.11, 0, 0.01, 0);
        spawn(client, pet, x + bx * 0.13, y - 0.02, z + bz * 0.13, 0, 0.015, 0);
    }

    private void emitAura(MinecraftClient client, PlayerEntity p) {
        ParticleEffect particle = CosmeticCatalog.auraParticle(config.auraStyle);
        int points = config.particleDensity == 1 ? 8 : config.particleDensity == 2 ? 12 : 16;
        double phase = tick * 0.07;
        for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2.0 * i / points) + phase;
            double radius = 0.63 + Math.sin(phase + i) * 0.035;
            spawn(client, particle, p.getX() + Math.cos(a) * radius,
                    p.getY() + 0.10 + Math.sin(phase * 1.4 + i) * 0.06,
                    p.getZ() + Math.sin(a) * radius, 0, 0.012, 0);
        }
    }

    private void emitHand(MinecraftClient client, PlayerEntity p, double rx, double rz,
                          double bx, double bz, ParticleEffect particle) {
        double x = p.getX() + rx * 0.38 - bx * 0.32;
        double y = p.getY() + 0.88;
        double z = p.getZ() + rz * 0.38 - bz * 0.32;
        spawn(client, particle, x, y, z, 0, 0.02, 0);
        if (config.particleDensity >= 2) {
            spawn(client, particle, x + rx * 0.08, y + 0.10, z + rz * 0.08, 0, 0.015, 0);
        }
    }

    private void emitJumpCrystal(MinecraftClient client, PlayerEntity p) {
        int points = config.particleDensity == 1 ? 6 : 10;
        double phase = tick * 0.16;
        for (int i = 0; i < points; i++) {
            double a = Math.PI * 2 * i / points + phase;
            double r = 0.36;
            spawn(client, ParticleTypes.END_ROD, p.getX() + Math.cos(a) * r,
                    p.getY() + 0.08 + (i % 2) * 0.10, p.getZ() + Math.sin(a) * r, 0, 0.035, 0);
        }
    }

    private void emitSkyMood(MinecraftClient client, PlayerEntity p) {
        ParticleEffect sky = CosmeticCatalog.skyParticle(config.skyStyle);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = config.particleDensity == 1 ? 1 : config.particleDensity == 2 ? 2 : 3;
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble(0, Math.PI * 2);
            double radius = random.nextDouble(5.0, 13.0);
            double x = p.getX() + Math.cos(angle) * radius;
            double z = p.getZ() + Math.sin(angle) * radius;
            double y = p.getY() + random.nextDouble(7.0, 13.0);
            spawn(client, sky, x, y, z, 0, 0.008, 0);
        }
    }

    private void emitOrbitStars(MinecraftClient client, PlayerEntity p) {
        ParticleEffect star = ParticleTypes.END_ROD;
        double phase = tick * 0.09;
        for (int i = 0; i < 3; i++) {
            double a = phase + (Math.PI * 2.0 * i / 3.0);
            double radius = 0.92;
            spawn(client, star, p.getX() + Math.cos(a) * radius,
                    p.getY() + 1.45 + Math.sin(phase * 1.4 + i) * 0.12,
                    p.getZ() + Math.sin(a) * radius, 0, 0.012, 0);
        }
    }

    private void emitFootsteps(MinecraftClient client, PlayerEntity p) {
        if (tick % 5 != 0) return;
        ParticleEffect trail = CosmeticCatalog.capeParticle(config.capeStyle);
        double side = ThreadLocalRandom.current().nextBoolean() ? 0.16 : -0.16;
        spawn(client, trail, p.getX() + side, p.getY() + 0.08, p.getZ(),
                0, 0.018, 0);
    }

    private void emitCrown(MinecraftClient client, PlayerEntity p) {
        if (tick % 2 != 0) return;
        for (int i = 0; i < 5; i++) {
            double a = Math.PI * (i / 4.0);
            spawn(client, ParticleTypes.END_ROD, p.getX() + Math.cos(a) * 0.24,
                    p.getY() + 2.02 + (i % 2) * 0.06, p.getZ() + Math.sin(a) * 0.24,
                    0, 0.008, 0);
        }
    }

    private void emitShoulderWisp(MinecraftClient client, PlayerEntity p, double rx, double rz) {
        double phase = tick * 0.08;
        spawn(client, ParticleTypes.SOUL, p.getX() + rx * 0.34 + Math.cos(phase) * 0.06,
                p.getY() + 1.48 + Math.sin(phase * 1.7) * 0.07,
                p.getZ() + rz * 0.34, 0, 0.012, 0);
    }

    private void emitHalo(MinecraftClient client, PlayerEntity p) {
        int points = 8;
        double phase = tick * 0.05;
        for (int i = 0; i < points; i++) {
            double a = Math.PI * 2.0 * i / points + phase;
            spawn(client, ParticleTypes.END_ROD,
                    p.getX() + Math.cos(a) * 0.28, p.getY() + 2.12,
                    p.getZ() + Math.sin(a) * 0.28, 0, 0.004, 0);
        }
    }

    private static void spawn(MinecraftClient client, ParticleEffect particle, double x, double y, double z,
                              double vx, double vy, double vz) {
        if (client.world != null) client.world.addParticle(particle, x, y, z, vx, vy, vz);
    }
}
