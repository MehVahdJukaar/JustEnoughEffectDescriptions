package net.mehvahdjukaar.jeed.forge;

import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.forge.NativeCompat;
import net.mehvahdjukaar.jeed.compat.forge.StylishEffectsCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@Mod(Jeed.MOD_ID)
public class JeedImpl {

    public JeedImpl() {
        if (!ModList.get().isLoaded("jei") && !ModList.get().isLoaded("roughlyenoughitems")) {
            Jeed.LOGGER.error("Jepp requires either JEI or REI mods. None of them was found");
        }

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPES_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);

        createConfigs();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            //credits to Fuzss for all the Stylish Effects mod compat
            if (ModList.get().isLoaded("stylisheffects")) {
                StylishEffectsCompat.init();
            } else {
                NativeCompat.init();
            }

        }
    }

    private static ForgeConfigSpec.BooleanValue effectBox;
    private static ForgeConfigSpec.BooleanValue ingredientsList;
    private static ForgeConfigSpec.BooleanValue effectColor;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> hiddenEffects;

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, Jeed.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, Jeed.MOD_ID);

    public static final RegistryObject<RecipeType<EffectProviderRecipe>> EFFECT_PROVIDER_TYPE = RECIPE_TYPES.register(
            "effect_provider", () -> RecipeType.simple(Jeed.res("effect_provider")));
    public static final RegistryObject<RecipeType<PotionProviderRecipe>> POTION_PROVIDER_TYPE = RECIPE_TYPES.register(
            "potion_provider", () -> RecipeType.simple(Jeed.res("potion_provider")));

    public static final RegistryObject<RecipeSerializer<EffectProviderRecipe>> EFFECT_PROVIDER_SERIALIZER = RECIPES_SERIALIZERS.register(
            "effect_provider", EffectProviderRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<PotionProviderRecipe>> POTION_PROVIDER_SERIALIZER = RECIPES_SERIALIZERS.register(
            "potion_provider", PotionProviderRecipe.Serializer::new);


    private static void createConfigs() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        effectColor = builder.comment("Show effect colors in tooltip")
                .define("effect_color", true);
        effectBox = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);
        hiddenEffects = builder.comment("A list of effects that should not be registered nor shown in JEI")
                .defineList("hidden_effects", Collections.singletonList(""), String.class::isInstance);
        ingredientsList = builder.comment("Show ingredients list along with an effect description")
                .define("ingredients_list", true);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    public static RecipeSerializer<?> getEffectProviderSerializer() {
        return EFFECT_PROVIDER_SERIALIZER.get();
    }

    public static RecipeType<EffectProviderRecipe> getEffectProviderType() {
        return EFFECT_PROVIDER_TYPE.get();
    }

    public static RecipeSerializer<?> getPotionProviderSerializer() {
        return POTION_PROVIDER_SERIALIZER.get();
    }

    public static RecipeType<PotionProviderRecipe> getPotionProviderType() {
        return POTION_PROVIDER_TYPE.get();
    }

    public static Collection<String> getHiddenEffects() {
        return (Collection<String>) hiddenEffects.get();
    }


    public static boolean hasIngredientList() {
        return ingredientsList.get();
    }

    public static boolean hasEffectBox() {
        return effectBox.get();
    }

    public static boolean hasEffectColor() {
        return effectColor.get();
    }


}