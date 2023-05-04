package net.mehvahdjukaar.jeed;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Author: MehVahdJukaar
 */
public class Jeed {

    public static final String MOD_ID = "jeed";

    public static final Logger LOGGER = LogManager.getLogger("Jeed");

    public static final IModCompat MOD_COMPAT = initModCompat();
    public static IPlugin PLUGIN;

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

    @ExpectPlatform
    public static boolean hasEffectColor() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        throw new AssertionError();
    }

    public static List<MobEffect> getEffectList() {
        return Registry.MOB_EFFECT.stream()
                .filter(e -> !Jeed.getHiddenEffects().contains(Registry.MOB_EFFECT.getKey(e).toString()))
                .toList();
    }

}
