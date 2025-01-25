package net.mehvahdjukaar.jeed;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.ID_COMPARATOR;

/**
 * Author: MehVahdJukaar
 */
public class Jeed {

    public static final String MOD_ID = "jeed";

    public static final Logger LOGGER = LogManager.getLogger("Jeed");

    public static IPlugin PLUGIN;

    public static boolean EMI = false;
    public static boolean REI = false;

    public static ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }


    //TODO: mixin into EMI mixin so its inventory rendered effects are clickable


    @Contract
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

    @ExpectPlatform
    public static boolean suppressVanillaTooltips() {
        throw new AssertionError();
    }

    private static final TagKey<MobEffect> HIDDEN = TagKey.create(Registries.MOB_EFFECT, res("hidden"));

    public static List<Holder.Reference<MobEffect>> getEffectList() {
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(e -> !e.is(HIDDEN) && !Jeed.getHiddenEffects().contains(e.key().toString()))
                .sorted((a, b) -> ID_COMPARATOR.compare(a.key().location(), b.key().location()))
                .toList();
    }

    //TODO: effect icons tooltip in potions

}
