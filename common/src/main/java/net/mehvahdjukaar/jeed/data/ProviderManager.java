package net.mehvahdjukaar.jeed.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Effect and potion providers used to be datapack recipes, but since 1.21.2 the client is no longer sent arbitrary
 * recipes, so they are now client resources under {@code assets/<namespace>/jeed_providers}.
 * <p>
 * Parsing needs a registry lookup, so it happens on demand once a level is available rather than at reload time.
 */
public class ProviderManager {

    private static final String DIRECTORY = "jeed_providers";

    private static List<EffectProvider> effectProviders = List.of();
    private static List<PotionProvider> potionProviders = List.of();
    //the registries the current lists were parsed against, so joining another world reparses
    private static Object parsedAgainst = null;

    public static void invalidate() {
        parsedAgainst = null;
        effectProviders = List.of();
        potionProviders = List.of();
    }

    public static List<EffectProvider> getEffectProviders() {
        loadIfNeeded();
        return effectProviders;
    }

    public static List<PotionProvider> getPotionProviders() {
        loadIfNeeded();
        return potionProviders;
    }

    private static void loadIfNeeded() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Object registries = mc.level.registryAccess();
        if (parsedAgainst == registries) return;

        List<EffectProvider> effects = new ArrayList<>();
        List<PotionProvider> potions = new ArrayList<>();

        RegistryOps<JsonElement> ops = mc.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        ResourceManager resourceManager = mc.getResourceManager();

        for (Map.Entry<Identifier, Resource> entry :
                resourceManager.listResources(DIRECTORY, p -> p.getPath().endsWith(".json")).entrySet()) {
            Identifier file = entry.getKey();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                ProviderEntry parsed = ProviderEntry.CODEC.parse(ops, json)
                        .getOrThrow(msg -> new IllegalStateException(file + ": " + msg));
                switch (parsed) {
                    case EffectProvider e -> effects.add(e);
                    case PotionProvider p -> potions.add(p);
                }
            } catch (Exception e) {
                Jeed.LOGGER.error("Failed to parse jeed provider file {}", file, e);
            }
        }

        effectProviders = List.copyOf(effects);
        potionProviders = List.copyOf(potions);
        parsedAgainst = registries;
    }
}
