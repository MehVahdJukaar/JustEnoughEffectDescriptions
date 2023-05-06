package net.mehvahdjukaar.jeed.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.fabric.NativeCompat;
import net.mehvahdjukaar.jeed.compat.fabric.StylishEffectsCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.List;

public class JeedImpl implements ModInitializer {

    public static final RecipeType<EffectProviderRecipe> EFFECT_PROVIDER_TYPE = Registry.register(Registry.RECIPE_TYPE,
            Jeed.res("effect_provider"), makeRecipe(Jeed.res("effect_provider")));
    public static final RecipeType<PotionProviderRecipe> POTION_PROVIDER_TYPE = Registry.register(Registry.RECIPE_TYPE,
            Jeed.res("potion_provider"), makeRecipe(Jeed.res("potion_provider")));

    public static final RecipeSerializer<EffectProviderRecipe> EFFECT_PROVIDER_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER,
            Jeed.res("effect_provider"), new EffectProviderRecipe.Serializer());
    public static final RecipeSerializer<PotionProviderRecipe> POTION_PROVIDER_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER,
            Jeed.res("potion_provider"), new PotionProviderRecipe.Serializer());

    static <T extends Recipe<?>> RecipeType<T> makeRecipe(ResourceLocation name) {
        final String toString = name.toString();
        return new RecipeType<T>() {
            public String toString() {
                return toString;
            }
        };
    }

    public static Collection<String> getHiddenEffects() {
        return List.of();
    }


    public static RecipeSerializer<?> getEffectProviderSerializer() {
        return EFFECT_PROVIDER_SERIALIZER;
    }

    public static RecipeType<EffectProviderRecipe> getEffectProviderType() {
        return EFFECT_PROVIDER_TYPE;
    }

    public static RecipeSerializer<?> getPotionProviderSerializer() {
        return POTION_PROVIDER_SERIALIZER;
    }

    public static RecipeType<PotionProviderRecipe> getPotionProviderType() {
        return POTION_PROVIDER_TYPE;
    }

    public static boolean hasIngredientList() {
        return true;
    }

    public static boolean hasEffectBox() {
        return true;
    }

    public static boolean hasEffectColor() {
        return true;
    }

    @Override
    public void onInitialize() {

        if (!FabricLoader.getInstance().isModLoaded("jei") && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            Jeed.LOGGER.error("Jeed requires either JEI or REI mods. None of them was found");
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            JeedClient.init();
        }
    }

    public static boolean rendersSlots() {
        return false;
    }
}
