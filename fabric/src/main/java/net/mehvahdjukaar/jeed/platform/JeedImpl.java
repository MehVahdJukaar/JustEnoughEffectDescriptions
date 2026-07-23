package net.mehvahdjukaar.jeed.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.Jeed;

import java.util.Collection;
import java.util.List;

public class JeedImpl implements ModInitializer {

    @Override
    public void onInitialize() {
        if (!FabricLoader.getInstance().isModLoaded("jei") && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")
                && !FabricLoader.getInstance().isModLoaded("emi")) {
            Jeed.LOGGER.error("Jeed requires either JEI, REI or EMI mods. None of them was found");
        }
        Jeed.EMI = FabricLoader.getInstance().isModLoaded("emi");
        Jeed.REI = FabricLoader.getInstance().isModLoaded("roughlyenoughitems");

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            JeedClient.init();
        }
    }

    public static Collection<String> getHiddenEffects() {
        return List.of();
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


    public static boolean rendersSlots() {
        return false;
    }

    public static boolean suppressVanillaTooltips() {
        return true;
    }

    public static boolean ignoreDerivativePotions() {
        return true;
    }

    public static boolean sortIngredients(){
        return false;
    }

    private static final boolean EMI = FabricLoader.getInstance().isModLoaded("emi");
}
