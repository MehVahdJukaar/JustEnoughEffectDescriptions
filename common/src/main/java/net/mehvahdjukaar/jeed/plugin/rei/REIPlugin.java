package net.mehvahdjukaar.jeed.plugin.rei;

import dev.architectury.event.CompoundEventResult;
import me.shedaniel.rei.REIModMenuEntryPoint;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.plugin.rei.display.EffectInfoDisplay;
import net.mehvahdjukaar.jeed.plugin.rei.display.EffectInfoDisplayCategory;
import net.mehvahdjukaar.jeed.plugin.rei.ingredient.EffectInstanceDefinition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

@REIPluginClient
public class REIPlugin implements REIClientPlugin, IPlugin {

    public static final CategoryIdentifier<EffectInfoDisplay> EFFECTS_INFO_CATEGORY = CategoryIdentifier.of(Jeed.res("effects"));
    public static final EntryType<MobEffectInstance> EFFECT_ENTRY_TYPE = EntryType.deferred(new ResourceLocation("effect"));

    public REIPlugin() {
        Jeed.PLUGIN = this;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new EffectInfoDisplayCategory());
        registry.addWorkstations(EFFECTS_INFO_CATEGORY, EntryStacks.of(Items.POTION));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (MobEffect e : Jeed.getEffectList()) {
            registry.add(EffectInfoDisplay.create(e));
        }
    }

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(EFFECT_ENTRY_TYPE, new EffectInstanceDefinition());
        registry.registerBridge(EFFECT_ENTRY_TYPE, VanillaEntryTypes.ITEM, input -> {
            ItemStack item = input.cheatsAs().getValue();
            return CompoundEventResult.interruptTrue(Stream.of(EntryStacks.of(item)));
        });
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (var e : Registry.MOB_EFFECT) {
            registry.addEntry(EntryStack.of(EFFECT_ENTRY_TYPE, new MobEffectInstance(e)));
        }
    }

    @Override
    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        ViewSearchBuilder.builder().addRecipesFor(EntryStack.of(EFFECT_ENTRY_TYPE, effect)).open();
    }

    @Override
    public int getMaxTextWidth() {
        return EffectInfoDisplayCategory.RECIPE_WIDTH - EffectInfoDisplayCategory.SIZE_DIFF * 2;
    }

    @Override
    public int getMaxTextHeight() {
        return 99999;
    }
}
