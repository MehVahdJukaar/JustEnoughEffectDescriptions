package net.mehvahdjukaar.jeed;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Author: MehVahdJukaar
 */
public class Jeed {

    public static final String MOD_ID = "jeed";

    public static final Logger LOGGER = LogManager.getLogger("Jeed");

    public static final IModCompat MOD_COMPAT = initModCompat();

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static void init() {

        //add here more mod compat for other mods that move effects

        MOD_COMPAT.registerHandlers();


        //TODO: check if this works with forge latest effect render event changes

        //TODO: render mob as entities instead of spawn eggs
    }

    @ExpectPlatform
    private static IModCompat initModCompat() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<String> getHiddenEffects() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RecipeSerializer<?> getEffectProviderSerializer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RecipeType<EffectProviderRecipe> getEffectProviderType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RecipeSerializer<?> getPotionProviderSerializer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RecipeType<PotionProviderRecipe> getPotionProviderType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasIngredientList() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasEffectBox() {
        throw new AssertionError();
    }

}
