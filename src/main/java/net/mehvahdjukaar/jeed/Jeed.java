package net.mehvahdjukaar.jeed;

import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MehVahdJukaar
 */
@Mod(Jeed.MOD_ID)
public class Jeed {
    public static final String MOD_ID = "jeed";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }


    private static final Logger LOGGER = LogManager.getLogger();

    public static ForgeConfigSpec.BooleanValue EFFECT_BOX;

    public Jeed() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        EFFECT_BOX = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());

        bus.addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);
    }

    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(EffectProviderRecipe.SERIALIZER);
        event.getRegistry().register(PotionProviderRecipe.SERIALIZER);

    }

}
