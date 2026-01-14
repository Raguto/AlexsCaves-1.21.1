package com.github.alexmodguy.alexscaves.server.level.surface;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class ACSurfaceRules {

    private static final int MAGNETIC_CAVES_OFFSET = 0;
    private static final int PRIMORDIAL_CAVES_OFFSET = 1;
    private static final int TOXIC_CAVES_OFFSET = 2;
    private static final int ABYSSAL_CHASM_OFFSET = 3;
    private static final int FORLORN_HOLLOWS_OFFSET = 4;
    private static final int CANDY_CAVITY_OFFSET = 5;

    public static void setup() {
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(MAGNETIC_CAVES_OFFSET), createMagneticCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(PRIMORDIAL_CAVES_OFFSET), createPrimordialCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(TOXIC_CAVES_OFFSET), createToxicCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(ABYSSAL_CHASM_OFFSET), createAbyssalChasmRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(FORLORN_HOLLOWS_OFFSET), createForlornHollowsRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(ACSurfaceRuleConditionRegistry.acBiomeCondition(CANDY_CAVITY_OFFSET), createCandyCavityRules());
    }

    public static SurfaceRules.RuleSource createMagneticCavesRules() {
        SurfaceRules.RuleSource galena = SurfaceRules.state(ACBlockRegistry.GALENA.get().defaultBlockState());
        SurfaceRules.RuleSource scarlet = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_SCARLET.get().defaultBlockState());
        SurfaceRules.RuleSource azure = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_AZURE.get().defaultBlockState());
        SurfaceRules.RuleSource neutral = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_NEUTRAL.get().defaultBlockState());
        SurfaceRules.ConditionSource azureCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.025F, 0.025F, 90, 1F, 0);
        SurfaceRules.ConditionSource scarletCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.025F, 0.025F, 90, 1F, 1);
        return SurfaceRules.sequence(
            bedrock(),
            SurfaceRules.ifTrue(azureCondition, SurfaceRules.ifTrue(scarletCondition, neutral)),
            SurfaceRules.ifTrue(scarletCondition, scarlet),
            SurfaceRules.ifTrue(azureCondition, azure),
            galena
        );
    }

    public static SurfaceRules.RuleSource createPrimordialCavesRules() {
        SurfaceRules.RuleSource limestone = SurfaceRules.state(ACBlockRegistry.LIMESTONE.get().defaultBlockState());
        SurfaceRules.RuleSource grass = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
        SurfaceRules.RuleSource packedMud = SurfaceRules.state(Blocks.PACKED_MUD.defaultBlockState());
        SurfaceRules.RuleSource dirtOrPackedMud = SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.12D, 0.2D), packedMud),
            dirt
        );
        SurfaceRules.ConditionSource isUnderwater = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.RuleSource grassWaterChecked = SurfaceRules.sequence(
            SurfaceRules.ifTrue(isUnderwater, grass),
            dirtOrPackedMud
        );
        SurfaceRules.RuleSource floorRules = SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassWaterChecked),
            SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirtOrPackedMud)
        );
        return SurfaceRules.sequence(
            bedrock(),
            floorRules,
            createBands(15, 1, 20, Blocks.SANDSTONE.defaultBlockState()),
            limestone
        );
    }

    public static SurfaceRules.RuleSource createToxicCavesRules() {
        SurfaceRules.RuleSource radrock = SurfaceRules.state(ACBlockRegistry.RADROCK.get().defaultBlockState());
        SurfaceRules.RuleSource mud = SurfaceRules.state(Blocks.MUD.defaultBlockState());
        SurfaceRules.ConditionSource belowY30 = SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(30), 0));
        return SurfaceRules.sequence(
            bedrock(),
            SurfaceRules.ifTrue(belowY30, SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, mud)),
            radrock
        );
    }

    public static SurfaceRules.RuleSource createAbyssalChasmRules() {
        SurfaceRules.RuleSource abyssmarine = SurfaceRules.state(ACBlockRegistry.ABYSSMARINE.get().defaultBlockState());
        SurfaceRules.RuleSource deepslate = SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState());
        SurfaceRules.RuleSource stone = SurfaceRules.state(Blocks.STONE.defaultBlockState());
        SurfaceRules.ConditionSource normalDeepslateCondition = SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8));
        SurfaceRules.RuleSource stoneOrDeepslate = SurfaceRules.sequence(
            SurfaceRules.ifTrue(normalDeepslateCondition, deepslate),
            stone
        );
        return SurfaceRules.sequence(
            bedrock(),
            SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, stoneOrDeepslate),
            SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), deepslate),
            abyssmarine
        );
    }

    public static SurfaceRules.RuleSource createForlornHollowsRules() {
        SurfaceRules.RuleSource mud = SurfaceRules.state(Blocks.PACKED_MUD.defaultBlockState());
        SurfaceRules.RuleSource guanostone = SurfaceRules.state(ACBlockRegistry.GUANOSTONE.get().defaultBlockState());
        SurfaceRules.RuleSource corpolith = SurfaceRules.state(ACBlockRegistry.COPROLITH.get().defaultBlockState());
        SurfaceRules.ConditionSource corpolithCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.2F, 0.4F, 40, 6F, 3);
        return SurfaceRules.sequence(
            bedrock(),
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, mud),
            SurfaceRules.ifTrue(corpolithCondition, corpolith),
            guanostone
        );
    }

    public static SurfaceRules.RuleSource createCandyCavityRules() {
        SurfaceRules.RuleSource chocolate = SurfaceRules.state(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState());
        SurfaceRules.RuleSource frostedChocolate = SurfaceRules.state(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get().defaultBlockState());
        SurfaceRules.RuleSource cake = SurfaceRules.state(ACBlockRegistry.CAKE_LAYER.get().defaultBlockState());
        SurfaceRules.ConditionSource isUnderwater = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.RuleSource frostedChocolateWaterChecked = SurfaceRules.ifTrue(isUnderwater, frostedChocolate);
        SurfaceRules.RuleSource floorRules = SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, frostedChocolateWaterChecked),
            SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, chocolate)
        );
        return SurfaceRules.sequence(
            bedrock(),
            floorRules,
            createBands(20, 2, 10, ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState()),
            cake
        );
    }

    private static SurfaceRules.RuleSource bedrock() {
        SurfaceRules.RuleSource bedrock = SurfaceRules.state(Blocks.BEDROCK.defaultBlockState());
        SurfaceRules.ConditionSource bedrockCondition = SurfaceRules.verticalGradient("bedrock", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5));
        return SurfaceRules.ifTrue(bedrockCondition, bedrock);
    }

    private static SurfaceRules.RuleSource createBands(int layers, int layerThickness, int layerDistance, BlockState state) {
        SurfaceRules.RuleSource bandBlock = SurfaceRules.state(state);
        SurfaceRules.RuleSource[] ruleSources = new SurfaceRules.RuleSource[layers];
        for (int i = 1; i <= layers; i++) {
            int yDown = i * layerDistance;
            int extra = i % 3 == 0 ? 1 : 0;
            SurfaceRules.ConditionSource layer1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62 - yDown), 0);
            SurfaceRules.ConditionSource layer2 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62 + extra + layerThickness - yDown), 0);
            ruleSources[i - 1] = SurfaceRules.ifTrue(layer1, 
                SurfaceRules.ifTrue(SurfaceRules.not(layer2), 
                    SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.7D, 0.8D), bandBlock)));
        }
        return SurfaceRules.sequence(ruleSources);
    }
}
