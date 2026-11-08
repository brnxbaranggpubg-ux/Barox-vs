# BAROX VS — v001

Client-side visual-cosmetics mod for Minecraft Java 1.21.4 + Fabric.

## Cosmetics
- Wings: Crystal, Angel, Demon, Butterfly, Void and Dragon particle styles.
- Cape trails and follower-pet particle mascots.
- Aura rings, hand glow, sword trail and airborne Jump Crystal.
- Ambient sky-mood particles, orbiting stars, footstep sparkles and a halo.
- More wing, pet and aura particle styles; color palettes, particle density and HUD placement.
- Settings saved in `config/baroxvs.json`.

## Controls
- Right Shift: open the wardrobe.
- Right Alt: toggle the HUD.
- Keybinds can be changed in Options → Controls.

## Build
Requires Java 21 and internet access for Gradle dependencies.

```bash
gradle clean build --no-daemon
```

The remapped release JAR is named `BAROX-VS-v001.jar` and is built by GitHub Actions.

## Compatibility
- Minecraft Java 1.21.4
- Fabric Loader 0.16.10+
- Fabric API 0.119.2+1.21.4
- Java 21

## Important
These effects are local particle visuals. Other players will not see them automatically. This build does not add real 3D pet entities, true wing models, or replace the skybox.
