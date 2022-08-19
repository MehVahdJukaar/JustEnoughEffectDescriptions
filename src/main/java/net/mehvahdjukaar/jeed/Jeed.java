package net.mehvahdjukaar.jeed;

import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.compat.NativeModCompat;
import net.mehvahdjukaar.jeed.compat.StylishEffectsCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@Mod(Jeed.MOD_ID)
public class Jeed {

    public static final String MOD_ID = "jeed";

    private static final Logger LOGGER = LogManager.getLogger();


    public static ForgeConfigSpec.BooleanValue EFFECT_BOX;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> HIDDEN_EFFECTS;

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, MOD_ID);

    public static final RegistryObject<RecipeType<?>> EFFECT_PROVIDER_TYPE = RECIPE_TYPES.register(
            "effect_provider",()->RecipeType.simple(res("effect_provider")));
    public static final RegistryObject<RecipeType<?>> POTION_PROVIDER_TYPE = RECIPE_TYPES.register(
            "potion_provider",()->RecipeType.simple(res("potion_provider")));

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

    }

    private static void createConfigs() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        EFFECT_BOX = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);
        HIDDEN_EFFECTS = builder.comment("A list of effects that should not be registered nor shown in JEI")
                .defineList("hidden_effects", Collections.singletonList(""), o -> o instanceof String);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

}
