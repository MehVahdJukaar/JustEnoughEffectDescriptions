package net.mehvahdjukaar.jeed.plugin.rei;

import dev.architectury.event.CompoundEventResult;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mehvahdjukaar.jeed.plugin.rei.ingredient.EffectInstanceDefinition;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public class REICommonPluginImpl implements REICommonPlugin {

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(REIPlugin.EFFECT_ENTRY_TYPE, new EffectInstanceDefinition());
        registry.registerBridge(REIPlugin.EFFECT_ENTRY_TYPE, VanillaEntryTypes.ITEM, input -> {
            ItemStack item = input.cheatsAs().getValue();
            return CompoundEventResult.interruptTrue(Stream.of(EntryStacks.of(item)));
        });
    }
}
