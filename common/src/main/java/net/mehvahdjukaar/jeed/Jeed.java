package net.mehvahdjukaar.jeed;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
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

    public static IPlugin PLUGIN;

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
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
    public static boolean rendersSlots() {
        throw new AssertionError();
    }

    private static final TagKey<MobEffect> HIDDEN = TagKey.create(Registries.MOB_EFFECT, res("hidden"));

    public static <T> boolean isTagged(T entry, Registry<T> registry, TagKey<T> tag) {
        return registry.getHolder(registry.getId(entry)).map(h -> h.is(tag)).orElse(false);
    }

    public static List<MobEffect> getEffectList() {
        return BuiltInRegistries.MOB_EFFECT.stream()
                .filter(e -> !isTagged(e, BuiltInRegistries.MOB_EFFECT, HIDDEN) && !Jeed.getHiddenEffects().contains(BuiltInRegistries.MOB_EFFECT.getKey(e).toString()))
                .toList();
    }

    //TODO: effect icons tooltip in potions

}
