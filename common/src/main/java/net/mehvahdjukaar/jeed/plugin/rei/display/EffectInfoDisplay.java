package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;

import java.util.List;

public class EffectInfoDisplay extends EffectInfo implements Display {

    private final List<EntryIngredient> inputEntries;
    private final List<EntryIngredient> outputEntries;

    protected EffectInfoDisplay(MobEffectInstance effectInstance, List<FormattedText> description) {
        super(effectInstance, description);
        this.inputEntries = inputItems.stream().map(EntryIngredients::of).toList();
        this.outputEntries = List.of(EntryIngredient.of(EntryStack.of(REIPlugin.EFFECT_ENTRY_TYPE,effectInstance)));
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


    public static List<EffectInfoDisplay> create(MobEffect effect) {
        return create(effect, EffectInfoDisplay::new);
    }
}
