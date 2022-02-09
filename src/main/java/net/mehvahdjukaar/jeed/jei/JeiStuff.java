package net.mehvahdjukaar.jeed.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class JeiStuff {

    public static List<ItemStack> getInputItems(List<ItemStack> inputItems) {
        IIngredientVisibility ingredientVisibility = JEIPlugin.JEI_INGREDIENT_VISIBILITY;
        return inputItems.stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> ingredientVisibility.isIngredientVisible(VanillaTypes.ITEM, s))
                .collect(Collectors.toList());

    }

}
