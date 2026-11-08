# Open-source research notes

The project was scoped against public Minecraft cosmetic-mod examples:

- `coding-jona/buildmc-cosmetics` (MIT): https://github.com/coding-jona/buildmc-cosmetics — a newer-version crystal-wing implementation. Its MIT license permits reuse with attribution, but it targets Minecraft 1.21.11 and uses a more involved mesh/renderer implementation. No code from it is copied into this 1.21.4 build.
- `Zik9k/fabric-cosmetics-mod`: https://github.com/Zik9k/fabric-cosmetics-mod — describes hats/capes/wings for 1.21.4, but the repository metadata did not declare an open-source license. Its code was not reused.

BAROX VS therefore implements its current effects independently using the 1.21.4 Fabric client particle API. Before importing code from another project, its license and version compatibility must be checked.
