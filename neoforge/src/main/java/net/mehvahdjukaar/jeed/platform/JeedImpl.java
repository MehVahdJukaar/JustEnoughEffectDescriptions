package net.mehvahdjukaar.jeed.platform;

import net.mehvahdjukaar.jeed.Jeed;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@Mod(Jeed.MOD_ID)
public class JeedImpl {

    public JeedImpl(IEventBus bus) {
        if (!ModList.get().isLoaded("jei") && !ModList.get().isLoaded("roughlyenoughitems")
                && !ModList.get().isLoaded("emi")) {
            Jeed.LOGGER.error("Jepp requires either JEI, REI or EMI mods. None of them was found");
        }

        Jeed.EMI = ModList.get().isLoaded("emi");
        Jeed.REI = ModList.get().isLoaded("roughlyenoughitems");

        createConfigs();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            JeedClient.init(bus);
        }
    }

    private static ModConfigSpec.BooleanValue effectBox;
    private static ModConfigSpec.BooleanValue ignoreDerivative;
    private static ModConfigSpec.BooleanValue sortIngredients;
    private static ModConfigSpec.BooleanValue renderSlots;
    private static ModConfigSpec.BooleanValue suppressVanillaTooltips;
    private static ModConfigSpec.BooleanValue ingredientsList;
    private static ModConfigSpec.BooleanValue effectColor;
    private static ModConfigSpec.ConfigValue<List<? extends String>> hiddenEffects;

    private static void createConfigs() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        effectColor = builder.comment("Show effect colors in tooltip")
                .define("effect_color", true);
        effectBox = builder.comment("Draw a black box behind effect icons")
                .define("effect_box", true);
        ignoreDerivative = builder.comment("Ignore derivative potions (long and strong) when showing effects")
                .define("ignore_derivative_potions", true);
        sortIngredients = builder.comment("Sort ingredients list by their ID")
                .define("sort_ingredients", false);
        hiddenEffects = builder.comment("A list of effects that should not be registered nor shown in JEI/REI. You can also use the 'hidden' mob_effect tag")
                .defineList("hidden_effects", Collections.singletonList(""), String.class::isInstance);
        ingredientsList = builder.comment("Show ingredients list along with an effect description")
                .define("ingredients_list", true);
        renderSlots = builder.comment("Renders individual slots instead of a big one. Only works for REI")
                .define("render_slots", false);
        suppressVanillaTooltips = builder.comment("Removes vanilla tooltips rendered when an effect renders small (square box)")
                .define("replace_vanilla_tooltips", true);

        ModList.get().getModContainerById(Jeed.MOD_ID).get()
                .registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    public static Collection<String> getHiddenEffects() {
        return (Collection<String>) hiddenEffects.get();
    }


    public static boolean sortIngredients(){
        return sortIngredients.get();
    }

    public static boolean hasIngredientList() {
        return ingredientsList.get();
    }

    public static boolean hasEffectBox() {
        return effectBox.get();
    }

    public static boolean ignoreDerivativePotions() {
        return ignoreDerivative.get();
    }

    public static boolean hasEffectColor() {
        return effectColor.get();
    }

    public static boolean rendersSlots() {
        return renderSlots.get();
    }

    public static boolean suppressVanillaTooltips() {
        return suppressVanillaTooltips.get();
    }
}