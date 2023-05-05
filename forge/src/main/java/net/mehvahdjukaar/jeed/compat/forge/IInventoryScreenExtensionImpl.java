package net.mehvahdjukaar.jeed.compat.forge;

import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;
import net.minecraftforge.fml.ModList;

public class IInventoryScreenExtensionImpl {

    public static IInventoryScreenExtension createInstance() {
        //credits to Fuzss for all the Stylish Effects mod compat
        if (ModList.get().isLoaded("stylisheffects")) {
            return new StylishEffectsScreenExtension();
        } else {
            return new VanillaScreenExtension();
        }
    }
}
