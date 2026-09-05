package net.mehvahdjukaar.jeed.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JEEDProviderManager extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

    public static final JEEDProviderManager INSTANCE = new JEEDProviderManager();

    private static final String DIRECTORY = "jeed_providers";

    private static Map<Identifier, JsonElement> lazyUnparsedFiles = Map.of();
    private static List<EffectProvider> effectProviders = List.of();
    private static List<PotionProvider> potionProviders = List.of();

    public static List<EffectProvider> getEffectProviders() {
        return effectProviders;
    }

    public static List<PotionProvider> getPotionProviders() {
        return potionProviders;
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> files = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry :
                resourceManager.listResources(DIRECTORY, p -> p.getPath().endsWith(".json")).entrySet()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                files.put(entry.getKey(), JsonParser.parseReader(reader));
            } catch (Exception e) {
                Jeed.LOGGER.error("Failed to read jeed provider file {}", entry.getKey(), e);
            }
        }
        return files;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        lazyUnparsedFiles = files;
        Level level = Minecraft.getInstance().level;
        if (level != null) applyWithLevel(level.registryAccess());
    }

    public static void applyWithLevel(HolderLookup.Provider registries) {
        List<EffectProvider> effects = new ArrayList<>();
        List<PotionProvider> potions = new ArrayList<>();
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<Identifier, JsonElement> entry : lazyUnparsedFiles.entrySet()) {
            Identifier file = entry.getKey();
            try {
                ProviderEntry parsed = ProviderEntry.CODEC.parse(ops, entry.getValue())
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
    }

    public static void resetWithLevel() {
        effectProviders = List.of();
        potionProviders = List.of();
    }
}
