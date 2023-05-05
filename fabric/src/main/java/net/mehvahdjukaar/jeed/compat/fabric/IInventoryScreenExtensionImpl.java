package net.mehvahdjukaar.jeed.compat.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;

public class IInventoryScreenExtensionImpl {

    public static IInventoryScreenExtension createInstance() {
        //credits to Fuzss for all the Stylish Effects mod compat
        if (FabricLoader.getInstance().isModLoaded("stylisheffects")) {
            return new StylishEffectsScreenExtension();
        } else {
            return new VanillaScreenExtension();
        }
    }
}
