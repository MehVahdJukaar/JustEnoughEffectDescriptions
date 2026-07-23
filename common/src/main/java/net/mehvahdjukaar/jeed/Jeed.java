package net.mehvahdjukaar.jeed;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.ID_COMPARATOR;
import static net.mehvahdjukaar.jeed.common.Constants.NAMESPACE_COMPARATOR;

/**
 * Author: MehVahdJukaar
 */
public class Jeed {

    public static final String MOD_ID = "jeed";

    public static final Logger LOGGER = LogManager.getLogger("Jeed");

    public static IPlugin PLUGIN;

    public static boolean EMI = false;
    public static boolean REI = false;

    public static Identifier res(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }


    //TODO: mixin into EMI mixin so its inventory rendered effects are clickable


    @Contract
    @PlatformImpl
    public static Collection<String> getHiddenEffects() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean hasIngredientList() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean hasEffectBox() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean ignoreDerivativePotions() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean sortIngredients(){
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean hasEffectColor() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean rendersSlots() {
        throw new AssertionError();
    }

    @PlatformImpl
    public static boolean suppressVanillaTooltips() {
        throw new AssertionError();
    }

    public static final TagKey<MobEffect> HIDDEN = TagKey.create(Registries.MOB_EFFECT, res("hidden"));

    public static List<Holder.Reference<MobEffect>> getEffectList() {
        return BuiltInRegistries.MOB_EFFECT.listElements()
                .filter(e -> !e.is(HIDDEN) && !Jeed.getHiddenEffects().contains(e.key().toString()))
                .sorted((a, b) -> NAMESPACE_COMPARATOR.compare(a.key().identifier(), b.key().identifier()))
                .toList();
    }

    //TODO: effect icons tooltip in potions

}
