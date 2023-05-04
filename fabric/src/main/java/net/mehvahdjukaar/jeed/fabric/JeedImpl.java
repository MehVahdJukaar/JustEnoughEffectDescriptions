package net.mehvahdjukaar.jeed.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.compat.NativeModCompat;
import net.mehvahdjukaar.jeed.compat.StylishEffectsCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.List;

public class JeedImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if(!FabricLoader.getInstance().isModLoaded("jei") && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")){
            Jeed.LOGGER.error("Jepp requires either JEI or REI mods. None of them was found");
        }
    }


    public static Collection<String> getHiddenEffects() {
        return List.of();
    }

    public static IModCompat initModCompat() {
        //credits to Fuzss for all the Stylish Effects mod compat
        if (FabricLoader.getInstance().isModLoaded("stylisheffects")) {
            return new StylishEffectsCompat();
        } else {
            return new NativeModCompat();
        }
    }

    public static RecipeSerializer<?> getEffectProviderSerializer() {
    }

    public static RecipeType<EffectProviderRecipe> getEffectProviderType() {
    }

    public static RecipeSerializer<?> getPotionProviderSerializer() {
    }

    public static RecipeType<PotionProviderRecipe> getPotionProviderType() {
    }

    public static boolean hasIngredientList() {
    }

    public static boolean hasEffectBox() {
    }
}
