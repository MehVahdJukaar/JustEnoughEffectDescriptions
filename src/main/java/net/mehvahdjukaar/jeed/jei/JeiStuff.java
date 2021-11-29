package net.mehvahdjukaar.jeed.jei;

import mezz.jei.Internal;
import mezz.jei.ingredients.IngredientFilter;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class JeiStuff {

    public static List<ItemStack> getInputItems(List<ItemStack> inputItems) {
        IngredientFilter filter = Internal.getIngredientFilter();
        return inputItems.stream().filter(s -> !s.isEmpty())
                .filter(filter::isIngredientVisible).collect(Collectors.toList());

    }

}
