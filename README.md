# BAROX VS — v002

Minecraft Java 1.21.4 • Fabric • Java 21

A client-side cosmetics and HUD client with a responsive purple glass ClickGUI.

## GUI
- Responsive top navigation, left sidebar, multi-column module cards and quick settings.
- Search modules by name or description.
- Category pages, scrollable module lists, persistent switches and theme palette.
- Purple Dream visual style inspired by the project reference.

## Cosmetic modules
Wings, cape trail, follower pet, aura ring, hand glow, sword sparkle, Jump Crystal, orbit stars, footstep trail, halo, sparkle trail, heart trail, bubble trail, crown sparkles, shoulder wisp and ambient sky particles.

## HUD modules
FPS, coordinates, direction, biome, ping, local clock and compact BAROX status panel.

## Controls
- Right Shift: open BAROX VS GUI.
- Right Alt: toggle BAROX status HUD.
- Settings are saved to `config/baroxvs.json`.

## Build
`gradle clean build --no-daemon`

Output: `BAROX-VS-v002.jar`.

## Open-source research and credits
The feature research reviewed public projects and their documented approaches:
- [BetterF3](https://github.com/TreyRuffy/BetterF3) — MIT; configurable HUD design inspiration.
- [MiniHUD](https://github.com/maruohon/minihud) — LGPL-3.0; configurable information overlays.
- [HudFabric](https://modrinth.com/mod/hudfabric) — MIT; movable HUD module ideas.
- [Particle Effects](https://github.com/LopyMine/Particle-Effects) — visual particle presentation inspiration.
- [Finery](https://www.curseforge.com/minecraft/mc-mods/finery) — MIT; 3D wearable cosmetics reference for future model-based work.
- [Pi's Cosmetics](https://modrinth.com/project/piscosmetics) — MIT; animated cosmetic rendering reference.

No third-party source code or assets are copied into this build. This release implements its features directly using Fabric and vanilla Minecraft APIs. External projects have different dependencies and compatibility requirements.

## Scope
All BAROX modules are local cosmetics, HUD or interface options. This project does not include combat automation, movement automation, anti-cheat bypasses, packet manipulation, entity ESP or server exploit features. Cosmetic particle silhouettes are not real 3D wearable models, and ambient sky particles do not replace the actual skybox.
