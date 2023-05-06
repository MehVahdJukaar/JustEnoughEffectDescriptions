package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EffectInfoDisplay extends EffectInfo implements Display {

    private final List<EntryIngredient> inputEntries;
    private final List<EntryIngredient> outputEntries;
    private final List<Component> componentList;

    protected EffectInfoDisplay(MobEffectInstance effectInstance, List<ItemStack> inputs, Component description) {
        super(effectInstance, inputs, List.of(description));
        var list = new ArrayList<>(inputItems.stream().map(EntryIngredients::of).toList());
        this.outputEntries = List.of(EntryIngredient.of(EntryStack.of(REIPlugin.EFFECT_ENTRY_TYPE, effectInstance).normalize()));
        list.addAll(outputEntries);
        this.inputEntries = list.stream().toList();
        this.componentList = List.of(description);
    }

    public List<Component> getComponents() {
        return componentList;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputEntries;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputEntries;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.EFFECTS_INFO_CATEGORY;
    }

    public static EffectInfoDisplay create(MobEffect effect) {
        Component text = getDescription(effect);

        return new EffectInfoDisplay(new MobEffectInstance(effect), computeEffectProviders(effect), text);
    }
}
