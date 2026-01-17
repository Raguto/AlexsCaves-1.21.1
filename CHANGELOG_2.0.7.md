# AlexsCaves 2.0.7 Changelog

## Bug Fixes

### Forlorn Hollows Biome Generation
- **Fixed**: Forlorn Hollows biome structures (canyon and bridges) now generate correctly when the biome is enabled
- **Technical**: Added override to `generate()` method in ForlornCanyonStructure and ForlornBridgeStructure to bypass Minecraft's biome validation
- **Technical**: Implemented `findValidBiomeY()` method to locate the correct Y level where the voronoi biome exists
- **Issue**: Previously, structures would only generate when the biome was disabled due to Minecraft's post-validation rejecting the structure

### c2me Compatibility
- **Fixed**: Added try-catch block in ChunkGeneratorMixin to prevent IndexOutOfBoundsException crashes when using c2me (Concurrent Chunk Management Engine)
- **Note**: Harmless warnings about "block scheduled tick access in far chunk" and "PostProcessing" may still appear during initial world generation - these are cosmetic and do not affect performance or gameplay

### Neodymium Polarity Effect
- **Improved**: Significantly reduced magnetic pull strength to prevent players from getting stuck near neodymium blocks
- **Balanced**: Neodymium nodes now have 40% magnetic strength compared to pillars and blocks
- **Technical**: Reduced base pull force from 0.08F to 0.035F for nodes, kept 0.15F for pillars/blocks
- **Technical**: Reduced movement application multiplier from 0.02 to 0.005F for more subtle effect

### Camera Rotation on Magnetic Surfaces
- **Added**: Camera now properly rotates when walking on magnetic walls and ceilings
- **Technical**: Implemented camera pitch adjustment in ClientEvents.computeCameraAngles()
- **Effect**: 90-degree rotation for horizontal surfaces (walls), 180-degree rotation for ceiling
- **Smooth**: Rotation interpolates based on attachment progress for smooth transitions

### Debug Log Cleanup
- **Removed**: Eliminated verbose "[Forlorn] postProcess" and "[Forlorn] Carved X blocks" debug logs that were flooding server consoles
- **Impact**: Cleaner server logs during world generation

## Technical Details

### Files Modified
- `ForlornCanyonStructure.java` - Added generate() override and findValidBiomeY() method
- `ForlornBridgeStructure.java` - Added generate() override and findValidBiomeY() method
- `ChunkGeneratorMixin.java` - Added IndexOutOfBoundsException handling for c2me compatibility
- `MagnetUtil.java` - Reduced magnetic pull forces and added block-type strength multipliers
- `ClientEvents.java` - Added camera rotation logic for magnetic surfaces
- `ForlornCanyonStructurePiece.java` - Removed debug logging statements

### Known Issues
- c2me users may see harmless warnings in console during chunk generation - these can be ignored or suppressed via log4j2.xml configuration
- Warnings do not cause lag, crashes, or affect gameplay

## Compatibility
- Minecraft 1.21.1
- NeoForge
- Compatible with c2me (Concurrent Chunk Management Engine)
- Compatible with other world generation mods

## Credits
- Bug reports and testing by community members
- Fixes implemented for improved c2me compatibility and gameplay balance
