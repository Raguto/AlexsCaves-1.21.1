package com.github.alexmodguy.alexscaves.server.potion;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = AlexsCaves.MODID)
public class ACEffectRegistry {

    public static final DeferredRegister<MobEffect> DEF_REG = DeferredRegister.create(Registries.MOB_EFFECT, AlexsCaves.MODID);
    public static final DeferredRegister<Potion> POTION_DEF_REG = DeferredRegister.create(Registries.POTION, AlexsCaves.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> MAGNETIZING = DEF_REG.register("magnetizing", () -> new MagnetizedEffect());
    public static final DeferredHolder<MobEffect, MobEffect> STUNNED = DEF_REG.register("stunned", () -> new StunnedEffect());
    public static final DeferredHolder<MobEffect, MobEffect> RAGE = DEF_REG.register("rage", () -> new RageEffect());
    public static final DeferredHolder<MobEffect, MobEffect> IRRADIATED = DEF_REG.register("irradiated", () -> new IrradiatedEffect());
    public static final DeferredHolder<MobEffect, MobEffect> BUBBLED = DEF_REG.register("bubbled", () -> new BubbledEffect());
    public static final DeferredHolder<MobEffect, MobEffect> DEEPSIGHT = DEF_REG.register("deepsight", () -> new DeepsightEffect());
    public static final DeferredHolder<MobEffect, MobEffect> DARKNESS_INCARNATE = DEF_REG.register("darkness_incarnate", () -> new DarknessIncarnateEffect());
    public static final DeferredHolder<MobEffect, MobEffect> SUGAR_RUSH = DEF_REG.register("sugar_rush", () -> new SugarRushEffect());
    public static final DeferredHolder<Potion, Potion> MAGNETIZING_POTION = POTION_DEF_REG.register("magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_MAGNETIZING_POTION = POTION_DEF_REG.register("long_magnetizing", () -> new Potion(new MobEffectInstance(MAGNETIZING, 9600)));
    public static final DeferredHolder<Potion, Potion> DEEPSIGHT_POTION = POTION_DEF_REG.register("deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_DEEPSIGHT_POTION = POTION_DEF_REG.register("long_deepsight", () -> new Potion(new MobEffectInstance(DEEPSIGHT, 9600)));
    public static final DeferredHolder<Potion, Potion> GLOWING_POTION = POTION_DEF_REG.register("glowing", () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_GLOWING_POTION = POTION_DEF_REG.register("long_glowing", () -> new Potion(new MobEffectInstance(MobEffects.GLOWING, 9600)));
    public static final DeferredHolder<Potion, Potion> HASTE_POTION = POTION_DEF_REG.register("haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_HASTE_POTION = POTION_DEF_REG.register("long_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_HASTE_POTION = POTION_DEF_REG.register("strong_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> STRONG_HUNGER_POTION = POTION_DEF_REG.register("strong_hunger", () -> new Potion(new MobEffectInstance(MobEffects.HUNGER, 1800, 4)));
    public static final DeferredHolder<Potion, Potion> SUGAR_RUSH_POTION = POTION_DEF_REG.register("sugar_rush", () -> new Potion(new MobEffectInstance(SUGAR_RUSH, 1800)));
    public static final DeferredHolder<Potion, Potion> LONG_SUGAR_RUSH_POTION = POTION_DEF_REG.register("long_sugar_rush", () -> new Potion(new MobEffectInstance(SUGAR_RUSH, 3600)));

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.FERROUSLIME_BALL.get()), createPotion(MAGNETIZING_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(MAGNETIZING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_MAGNETIZING_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.LANTERNFISH.get()), createPotion(DEEPSIGHT_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(DEEPSIGHT_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_DEEPSIGHT_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.BIOLUMINESSCENCE.get()), createPotion(GLOWING_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(GLOWING_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_GLOWING_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.AWKWARD)), Ingredient.of(ACItemRegistry.CORRODENT_TEETH.get()), createPotion(HASTE_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(HASTE_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_HASTE_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(HASTE_POTION)), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_HASTE_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.STRONG_SWIFTNESS)), Ingredient.of(ACItemRegistry.SWEET_TOOTH.get()), createPotion(SUGAR_RUSH_POTION)));
        builder.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(SUGAR_RUSH_POTION)), Ingredient.of(Items.REDSTONE), createPotion(LONG_SUGAR_RUSH_POTION)));
    }

    public static void setup() {
        // Brewing recipes are now registered via RegisterBrewingRecipesEvent
    }

    public static ItemStack createPotion(DeferredHolder<Potion, Potion> potion) {
        return createPotion(potion.getDelegate());
    }

    public static ItemStack createPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static ItemStack createSplashPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static ItemStack createLingeringPotion(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static ItemStack createJellybean(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(ACItemRegistry.JELLY_BEAN.get());
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }
}
