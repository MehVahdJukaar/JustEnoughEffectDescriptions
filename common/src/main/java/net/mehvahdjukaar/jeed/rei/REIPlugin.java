package net.mehvahdjukaar.jeed.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.rei.display.EffectInfoDisplay;
import net.mehvahdjukaar.jeed.rei.display.EffectInfoDisplayCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Items;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<EffectInfoDisplay> EFFECTS_INFO_CATEGORY = CategoryIdentifier.of(Jeed.res("effects"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new EffectInfoDisplayCategory());
        registry.addWorkstations(EFFECTS_INFO_CATEGORY, EntryStacks.of(Items.POTION));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (Holder<PaintingVariant> painting : Registry.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE)) {
            //PaintingInfo recipe = new EffectInfoDisplay(painting.value());
            //registry.add(recipe);
        }
    }

}
