package net.mehvahdjukaar.jeed.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.mehvahdjukaar.jeed.PaintingInfo;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Items;

import java.util.List;

public class PaintingInfoDisplay extends PaintingInfo implements Display {

    public PaintingInfoDisplay(PaintingVariant painting) {
        super(painting);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.of(Items.PAINTING));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(Items.PAINTING));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return JeppReiPlugin.PAINTING_INFO_TYPE;
    }
}
