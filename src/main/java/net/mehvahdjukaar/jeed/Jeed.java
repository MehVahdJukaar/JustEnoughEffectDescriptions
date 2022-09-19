package net.mehvahdjukaar.jeed;

import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

    public static boolean REI = false;

    public static ForgeConfigSpec.BooleanValue EFFECT_BOX;
    public static ForgeConfigSpec.BooleanValue INGREDIENTS_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> HIDDEN_EFFECTS;

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    private static final RegistryObject<RecipeSerializer<?>> EFFECT_PROVIDER = RECIPES.register("effect_provider", () -> EffectProviderRecipe.SERIALIZER);
    private static final RegistryObject<RecipeSerializer<?>> POTION_PROVIDER = RECIPES.register("potion_provider", () -> EffectProviderRecipe.SERIALIZER);

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public Jeed() {

        ModLoadingContext modLoader = ModLoadingContext.get();
        modLoader.registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPES.register(bus);
        bus.addListener(Jeed::init);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        EFFECT_BOX = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);
        INGREDIENTS_LIST = builder.comment("Show ingredients list along with an effect description")
                .define("ingredients_list", true);
        HIDDEN_EFFECTS = builder.comment("A list of effects that should not be registered nor shown on JEI")
                .defineList("hidden_effects", Collections.singletonList(""), o -> o instanceof String);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    public static void init(final FMLCommonSetupEvent event) {
        REI = ModList.get().isLoaded("roughlyenoughitems");
    }

}
