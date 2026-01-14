# AlexsCaves 1.21.1 Biome Generation Fixes

## Summary
This document details all fixes made to address biome generation issues in the 1.21.1 port. The core issue was that Minecraft 1.21 changed how biome sampling works internally, breaking the Voronoi-based biome injection system that worked in 1.20.1.

---

## Issue 1: Biome Blocks Not Generating on Walls/Ceilings

**Problem:** AC biome-specific blocks (radrock, galena, cake, limestone, etc.) were only appearing on floors, leaving vanilla stone/deepslate exposed on walls and ceilings.

**Root Cause:** Minecraft's `SurfaceRules.isBiome()` check samples biomes differently than the F3 display. The Voronoi injection worked for the F3 biome display but not for surface rule application on wall/ceiling blocks.

**Solution:** Created `ACBiomeConditionSource` in `ACSurfaceRuleConditionRegistry.java` - a custom surface rule condition that checks the Voronoi noise directly instead of relying on Minecraft's `isBiome()`.

**Files Modified:**
- `ACSurfaceRuleConditionRegistry.java` - Added `ACBiomeConditionSource` class
- `ACSurfaceRules.java` - Changed from `SurfaceRules.isBiome()` to `acBiomeCondition(rarityOffset)`

---

## Issue 2: Biome Blocks Extending to Surface

**Problem:** After fixing wall/ceiling generation, biome blocks were extending too high and appearing at the surface level.

**Solution:** Added `MAX_Y_LEVEL = 50` cap in `ACBiomeConditionSource` to prevent surface rules from applying above Y=50.

**Files Modified:**
- `ACSurfaceRuleConditionRegistry.java` - Added Y-level cap in `ACBiomeConditionSource.test()`

---

## Issue 3: Biome Blocks Not Wide Enough

**Problem:** Biome-specific blocks weren't covering the full width of the cave structures.

**Solution:** Added `BIOME_BOUNDARY_EXTENSION = 1.2D` multiplier in `ACBiomeRarity.java` to extend biome boundaries by 20%.

**Files Modified:**
- `ACBiomeRarity.java` - Added boundary extension multiplier

---

## Issue 4: Vanilla Structures Spawning in AC Biomes

**Problem:** Mineshafts, strongholds, trial chambers, and other vanilla structures were spawning inside AC biomes.

**Solution:** 
1. Created biome tag `has_no_vanilla_structures_in.json` containing all AC biomes
2. Extended structure check radius in mixins (mineshafts: 80 blocks, strongholds: 100 blocks, etc.)

**Files Modified:**
- `has_no_vanilla_structures_in.json` - Created tag file
- `MineshaftStructureMixin.java` - Extended check radius to 80 blocks
- `StrongholdStructureMixin.java` - Extended check radius to 100 blocks
- `RuinedPortalStructureMixin.java` - Extended check radius to 80 blocks
- `JigsawStructureMixin.java` - Extended check radius for trial chambers

---

## Issue 5: Abyssal Chasm Spawning Under Rivers/Lakes

**Problem:** Abyssal Chasm was spawning under any body of water, not just oceans.

**Solution:** Added double-check in `MultiNoiseBiomeSourceMixin` - the Voronoi cell center must be in ocean AND the actual block position must also have negative continentalness (< -0.5).

**Files Modified:**
- `MultiNoiseBiomeSourceMixin.java` - Added per-position continentalness check for Abyssal Chasm

---

## Issue 6: Dimension Restrictions

**Problem:** AC biomes were restricted to `minecraft:overworld` only.

**Solution:** Removed dimension restrictions - empty dimensions array now means biomes can spawn in any dimension. Users can restrict via config files.

**Files Modified:**
- `BiomeGenerationConfig.java` - Removed dimension restrictions, bumped CONFIG_VERSION to 7
- `BiomeGenerationNoiseCondition.java` - Updated `isInvalid()` to allow empty dimensions

---

## Issue 7: Console Log Spam

**Problem:** Multiple sources of annoying log spam during world generation.

**Solutions:**
1. Created `UtilMixin.java` to suppress "Detected setBlock in a far chunk" errors
2. Removed debug logging from `ACCommands.java` (locateacbiome command)
3. Silenced feature placement failure warnings in `ChunkGeneratorMixin.java`

**Files Modified:**
- `UtilMixin.java` - Created to suppress far chunk errors
- `ACCommands.java` - Removed debug logging
- `ChunkGeneratorMixin.java` - Silenced feature placement warnings
- `alexscaves.mixins.json` - Added UtilMixin

---

## Issue 8: Too Many Geothermal Vents

**Problem:** Toxic Caves had excessive geothermal vent generation.

**Solution:** Reduced vent count from 100-150 to 2-5 per chunk.

**Files Modified:**
- `acid_vent.json` - Reduced count values

---

## Issue 9: Mud Spawning Too High in Toxic Caves

**Problem:** Mud was appearing at high Y levels in Toxic Caves.

**Solution:** Limited mud generation to below Y=30 in surface rules.

**Files Modified:**
- `ACSurfaceRules.java` - Added Y<30 condition for mud in Toxic Caves

---

## Issue 10: End Poem Modification Removed

**Problem:** AlexsCaves was modifying the vanilla end poem, which was unwanted.

**Solution:** Removed the end poem modification functionality.

**Files Modified:**
- Removed end poem related code/assets

---

## Issue 11: Submarine Camera Position

**Problem:** The submarine camera was not positioned correctly when riding the submarine.

**Solution:** Fixed submarine camera positioning to be in the correct location.

**Files Modified:**
- Submarine camera-related code

---

## Issue 12: Neodymium Polarity Not Working

**Problem:** Neodymium magnet polarity (attraction/repulsion) was completely non-functional.

**Solution:** Fixed the polarity mechanics so neodymium magnets properly attract and repel based on their polarity.

**Files Modified:**
- Neodymium magnet item/entity classes

---

## Issue 13: Darkness Outfit Flight Not Ending

**Problem:** The Darkness Outfit effect was not properly ending flight when the effect expired or was removed.

**Solution:** Fixed the flight termination logic so players properly stop flying when the Darkness Outfit effect ends.

**Files Modified:**
- Darkness Outfit effect/armor related classes

---

## Issue 14: Notor Scanning Shader Crash

**Problem:** The game would crash when Notor's scanning feature scanned the player due to a shader issue.

**Solution:** Fixed the shader crash when Notor scans the player.

**Files Modified:**
- Notor scanning/shader related code

---

## Issue 15: Primitive Club Damage and Attack Speed

**Problem:** The Primitive Club weapon did not have proper damage values and attack speed.

**Solution:** Fixed the damage and attack speed attributes for the Primitive Club.

**Files Modified:**
- Primitive Club item class

---

## Issue 16: Trilocaris Drowning

**Problem:** Trilocaris was drowning when it shouldn't - it's an aquatic creature.

**Solution:** Fixed the breathing/drowning logic for Trilocaris so it can properly survive underwater.

**Files Modified:**
- Trilocaris entity class

---

## Issue 17: Neodymium Density Too High

**Problem:** Neodymium ore/nodes were spawning too densely in Magnetic Caves.

**Solution:** Reduced the density of neodymium generation in Magnetic Caves.

**Files Modified:**
- Neodymium placed feature JSON files

---

## Issue 18: Holocoder and Hologram Projector Not Working

**Problem:** The Holocoder and Hologram Projector items were completely non-functional.

**Solution:** Fixed the Holocoder and Hologram Projector functionality so they work as intended.

**Files Modified:**
- Holocoder/Hologram Projector related classes

---

## Issue 19: Cave Codex Colors Incorrect

**Problem:** The Cave Codex items had incorrect/broken colors.

**Solution:** Fixed all Cave Codex color values to display correctly.

**Files Modified:**
- Cave Codex related classes

---

## Known Limitations

These issues cannot be fully resolved without rewriting the biome system to use TerraBlender:

1. **Mineshaft Corridors:** Mineshafts that start near (but outside) AC biomes can still extend their corridors into AC caves. The structure START is blocked, but corridors from nearby mineshafts can intrude.

2. **Biome Boundary Precision:** The Voronoi-based injection is fundamentally different from how Minecraft expects biomes to work. Some edge cases will always exist.

3. **1.20.1 Parity:** The 1.21 port will not perfectly match 1.20.1 behavior due to Minecraft's internal changes to biome sampling and surface rules.

---

## Config Version

Current config version: **7**

When CONFIG_VERSION is incremented, all user biome configs are automatically regenerated to pick up new defaults.
