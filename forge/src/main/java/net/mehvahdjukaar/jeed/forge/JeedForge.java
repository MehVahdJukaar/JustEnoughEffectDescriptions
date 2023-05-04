package net.mehvahdjukaar.jeed.forge;

import net.mehvahdjukaar.jeed.Jepp;
import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.compat.NativeModCompat;
import net.mehvahdjukaar.jeed.compat.StylishEffectsCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@Mod(Jepp.MOD_ID)
public class JeedForge {

    public JeedForge() {
        if(!ModList.get().isLoaded("jei") && !ModList.get().isLoaded("roughlyenoughitems")){
            Jeed.LOGGER.error("Jepp requires either JEI or REI mods. None of them was found");
        }
    }

    public static ForgeConfigSpec.BooleanValue EFFECT_BOX;
    public static ForgeConfigSpec.BooleanValue INGREDIENTS_LIST;
    public static ForgeConfigSpec.BooleanValue EFFECT_COLOR;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> HIDDEN_EFFECTS;

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, MOD_ID);

    public static final RegistryObject<RecipeType<?>> EFFECT_PROVIDER_TYPE = RECIPE_TYPES.register(
            "effect_provider", () -> RecipeType.simple(res("effect_provider")));
    public static final RegistryObject<RecipeType<?>> POTION_PROVIDER_TYPE = RECIPE_TYPES.register(
            "potion_provider", () -> RecipeType.simple(res("potion_provider")));

    public static final RegistryObject<RecipeSerializer<?>> EFFECT_PROVIDER_SERIALIZER = RECIPES_SERIALIZERS.register(
            "effect_provider", EffectProviderRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> POTION_PROVIDER_SERIALIZER = RECIPES_SERIALIZERS.register(
            "potion_provider", PotionProviderRecipe.Serializer::new);

    public static boolean REI;

    public static IModCompat MOD_COMPAT;

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public Jeed() {

        ModLoadingContext modLoader = ModLoadingContext.get();
        modLoader.registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPES_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);

        createConfigs();

        //mod compat


        REI = ModList.get().isLoaded("roughlyenoughitems");

        //credits to Fuzss for all the Stylish Effects mod compat
        if (ModList.get().isLoaded("stylisheffects")) {
            MOD_COMPAT = new StylishEffectsCompat();
        } else {
            MOD_COMPAT = new NativeModCompat();
        }
        //add here more mod compat for other mods that move effects

        MOD_COMPAT.registerHandlers();

        //TODO: check if this works with forge latest effect render event changes

        //TODO: render mob as entities instead of spawn eggs
        RecipeWrapper
    }

    private static void createConfigs() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        EFFECT_COLOR = builder.comment("Show effect colors in tooltip")
                .define("effect_color", true);
        EFFECT_BOX = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);
        HIDDEN_EFFECTS = builder.comment("A list of effects that should not be registered nor shown in JEI")
                .defineList("hidden_effects", Collections.singletonList(""), o -> o instanceof String);
        INGREDIENTS_LIST = builder.comment("Show ingredients list along with an effect description")
                .define("ingredients_list", true);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }



}

