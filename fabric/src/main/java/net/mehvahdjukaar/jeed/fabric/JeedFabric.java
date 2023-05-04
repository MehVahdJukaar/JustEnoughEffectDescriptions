package net.mehvahdjukaar.jeed.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.Jepp;

public class JeppFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if(!FabricLoader.getInstance().isModLoaded("jei") && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")){
            Jepp.LOGGER.error("Jepp requires either JEI or REI mods. None of them was found");
        }
    }
}
