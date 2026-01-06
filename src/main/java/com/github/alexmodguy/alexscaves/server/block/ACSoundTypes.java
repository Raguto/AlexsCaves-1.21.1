package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Supplier;

/**
 * Custom sound types for Alex's Caves blocks.
 * Uses LazySoundType to defer sound event resolution until after registration.
 */
public class ACSoundTypes {

    public static final SoundType NEODYMIUM = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.NEODYMIUM_BREAK, ACSoundRegistry.NEODYMIUM_STEP, 
        ACSoundRegistry.NEODYMIUM_PLACE, ACSoundRegistry.NEODYMIUM_BREAKING, ACSoundRegistry.NEODYMIUM_STEP);
    
    public static final SoundType METAL_SWARF = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.METAL_SWARF_BREAK, ACSoundRegistry.METAL_SWARF_STEP, 
        ACSoundRegistry.METAL_SWARF_PLACE, ACSoundRegistry.METAL_SWARF_BREAKING, ACSoundRegistry.METAL_SWARF_STEP);
    
    public static final SoundType SCRAP_METAL = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.SCRAP_METAL_BREAK, ACSoundRegistry.SCRAP_METAL_STEP, 
        ACSoundRegistry.SCRAP_METAL_PLACE, ACSoundRegistry.SCRAP_METAL_BREAKING, ACSoundRegistry.SCRAP_METAL_STEP);
    
    public static final SoundType METAL_SCAFFOLDING = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.SCRAP_METAL_BREAK, ACSoundRegistry.METAL_SCAFFOLDING_CLIMB, 
        ACSoundRegistry.SCRAP_METAL_PLACE, ACSoundRegistry.SCRAP_METAL_BREAKING, ACSoundRegistry.METAL_SCAFFOLDING_CLIMB);
    
    public static final SoundType AMBER = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.AMBER_BREAK, ACSoundRegistry.AMBER_STEP, 
        ACSoundRegistry.AMBER_PLACE, ACSoundRegistry.AMBER_BREAKING, ACSoundRegistry.AMBER_STEP);
    
    public static final SoundType AMBER_MONOLITH = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.AMBER_BREAK, ACSoundRegistry.AMBER_STEP, 
        ACSoundRegistry.AMBER_MONOLITH_PLACE, ACSoundRegistry.AMBER_BREAKING, ACSoundRegistry.AMBER_STEP);
    
    public static final SoundType PEWEN_BRANCH = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.PEWEN_BRANCH_BREAK, () -> SoundEvents.CHERRY_WOOD_STEP, 
        () -> SoundEvents.CHERRY_WOOD_PLACE, () -> SoundEvents.CHERRY_WOOD_HIT, () -> SoundEvents.CHERRY_WOOD_FALL);
    
    public static final SoundType FLOOD_BASALT = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.FLOOD_BASALT_BREAK, ACSoundRegistry.FLOOD_BASALT_STEP, 
        ACSoundRegistry.FLOOD_BASALT_PLACE, ACSoundRegistry.FLOOD_BASALT_BREAKING, ACSoundRegistry.FLOOD_BASALT_STEP);
    
    public static final SoundType RADROCK = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.RADROCK_BREAK, ACSoundRegistry.RADROCK_STEP, 
        ACSoundRegistry.RADROCK_PLACE, ACSoundRegistry.RADROCK_BREAKING, ACSoundRegistry.RADROCK_STEP);
    
    public static final SoundType SULFUR = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.SULFUR_BREAK, ACSoundRegistry.SULFUR_STEP, 
        ACSoundRegistry.SULFUR_PLACE, ACSoundRegistry.SULFUR_BREAKING, ACSoundRegistry.SULFUR_STEP);
    
    public static final SoundType URANIUM = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.URANIUM_BREAK, ACSoundRegistry.URANIUM_STEP, 
        ACSoundRegistry.URANIUM_PLACE, ACSoundRegistry.URANIUM_BREAKING, ACSoundRegistry.URANIUM_STEP);
    
    public static final SoundType HAZMAT_BLOCK = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.HAZMAT_BLOCK_BREAK, ACSoundRegistry.HAZMAT_BLOCK_STEP, 
        ACSoundRegistry.HAZMAT_BLOCK_PLACE, ACSoundRegistry.HAZMAT_BLOCK_BREAKING, ACSoundRegistry.HAZMAT_BLOCK_STEP);
    
    public static final SoundType CINDER_BLOCK = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.CINDER_BLOCK_BREAK, ACSoundRegistry.CINDER_BLOCK_STEP, 
        ACSoundRegistry.CINDER_BLOCK_PLACE, ACSoundRegistry.CINDER_BLOCK_BREAKING, ACSoundRegistry.CINDER_BLOCK_STEP);
    
    public static final SoundType UNREFINED_WASTE = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.UNREFINED_WASTE_BREAK, ACSoundRegistry.UNREFINED_WASTE_STEP, 
        ACSoundRegistry.UNREFINED_WASTE_PLACE, ACSoundRegistry.UNREFINED_WASTE_BREAKING, ACSoundRegistry.UNREFINED_WASTE_STEP);
    
    public static final SoundType NUCLEAR_BOMB = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.NUCLEAR_BOMB_BREAK, ACSoundRegistry.NUCLEAR_BOMB_STEP, 
        ACSoundRegistry.NUCLEAR_BOMB_PLACE, ACSoundRegistry.NUCLEAR_BOMB_BREAKING, ACSoundRegistry.NUCLEAR_BOMB_STEP);
    
    public static final SoundType TUBE_WORM = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.TUBE_WORM_BREAK, ACSoundRegistry.TUBE_WORM_STEP, 
        ACSoundRegistry.TUBE_WORM_PLACE, ACSoundRegistry.TUBE_WORM_BREAKING, ACSoundRegistry.TUBE_WORM_STEP);
    
    public static final SoundType THORNWOOD_BRANCH = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.THORNWOOD_BRANCH_BREAK, () -> SoundEvents.MANGROVE_ROOTS_STEP, 
        () -> SoundEvents.MANGROVE_ROOTS_PLACE, () -> SoundEvents.MANGROVE_ROOTS_HIT, () -> SoundEvents.MANGROVE_ROOTS_FALL);
    
    public static final SoundType PEERING_COPROLITH = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.PEERING_COPROLITH_BREAK, ACSoundRegistry.PEERING_COPROLITH_STEP, 
        ACSoundRegistry.PEERING_COPROLITH_PLACE, ACSoundRegistry.PEERING_COPROLITH_BREAKING, ACSoundRegistry.PEERING_COPROLITH_STEP);
    
    public static final SoundType MOTH_BALL = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.MOTH_BALL_PLACE, () -> SoundEvents.WOOL_STEP, 
        ACSoundRegistry.MOTH_BALL_PLACE, () -> SoundEvents.WOOL_HIT, () -> SoundEvents.WOOL_STEP);
    
    public static final SoundType BEHOLDER = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.BEHOLDER_BREAK, ACSoundRegistry.BEHOLDER_STEP, 
        ACSoundRegistry.BEHOLDER_PLACE, ACSoundRegistry.BEHOLDER_BREAKING, ACSoundRegistry.BEHOLDER_STEP);

    public static final SoundType SOFT_CANDY = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.SOFT_CANDY_BREAK, ACSoundRegistry.SOFT_CANDY_STEP, 
        ACSoundRegistry.SOFT_CANDY_PLACE, ACSoundRegistry.SOFT_CANDY_BREAKING, ACSoundRegistry.SOFT_CANDY_STEP);
    
    public static final SoundType DENSE_CANDY = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.DENSE_CANDY_BREAK, ACSoundRegistry.DENSE_CANDY_STEP, 
        ACSoundRegistry.DENSE_CANDY_PLACE, ACSoundRegistry.DENSE_CANDY_BREAKING, ACSoundRegistry.DENSE_CANDY_STEP);
    
    public static final SoundType HARD_CANDY = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.HARD_CANDY_BREAK, ACSoundRegistry.HARD_CANDY_STEP, 
        ACSoundRegistry.HARD_CANDY_PLACE, ACSoundRegistry.HARD_CANDY_BREAKING, ACSoundRegistry.HARD_CANDY_STEP);
    
    public static final SoundType SQUISHY_CANDY = new LazySoundType(1.0F, 1.0F, 
        ACSoundRegistry.SQUISHY_CANDY_BREAK, ACSoundRegistry.SQUISHY_CANDY_STEP, 
        ACSoundRegistry.SQUISHY_CANDY_PLACE, ACSoundRegistry.SQUISHY_CANDY_BREAKING, ACSoundRegistry.SQUISHY_CANDY_STEP);

    /**
     * A SoundType that lazily resolves sound events from suppliers.
     * This allows using DeferredHolder references without calling .get() during class loading.
     */
    private static class LazySoundType extends SoundType {
        private final Supplier<SoundEvent> breakSoundSupplier;
        private final Supplier<SoundEvent> stepSoundSupplier;
        private final Supplier<SoundEvent> placeSoundSupplier;
        private final Supplier<SoundEvent> hitSoundSupplier;
        private final Supplier<SoundEvent> fallSoundSupplier;

        public LazySoundType(float volume, float pitch, 
                           Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound,
                           Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, 
                           Supplier<SoundEvent> fallSound) {
            // Pass dummy sounds to parent - we override all methods anyway
            super(volume, pitch, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, 
                  SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
            this.breakSoundSupplier = breakSound;
            this.stepSoundSupplier = stepSound;
            this.placeSoundSupplier = placeSound;
            this.hitSoundSupplier = hitSound;
            this.fallSoundSupplier = fallSound;
        }

        @Override
        public SoundEvent getBreakSound() {
            return breakSoundSupplier.get();
        }

        @Override
        public SoundEvent getStepSound() {
            return stepSoundSupplier.get();
        }

        @Override
        public SoundEvent getPlaceSound() {
            return placeSoundSupplier.get();
        }

        @Override
        public SoundEvent getHitSound() {
            return hitSoundSupplier.get();
        }

        @Override
        public SoundEvent getFallSound() {
            return fallSoundSupplier.get();
        }
    }
}
