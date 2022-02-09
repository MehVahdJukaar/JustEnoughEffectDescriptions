package net.mehvahdjukaar.jeed.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class JeiStuff {

    public static List<ItemStack> getInputItems(List<ItemStack> inputItems) {
        IJeiRuntime jeiRuntime = JEIPlugin.JEI_RUNTIME;
        IIngredientVisibility ingredientVisibility = jeiRuntime.getIngredientVisibility();
        return inputItems.stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> ingredientVisibility.isIngredientVisible(VanillaTypes.ITEM, s))
                .collect(Collectors.toList());

    }

}
