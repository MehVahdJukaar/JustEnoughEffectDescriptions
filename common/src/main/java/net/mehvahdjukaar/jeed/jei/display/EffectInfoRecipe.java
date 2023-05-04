package net.mehvahdjukaar.jeed.jei.display;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.jei.JEIPlugin;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EffectInfoRecipe extends EffectInfo {

    public static final RecipeType<EffectInfoRecipe> TYPE = RecipeType.create(Jeed.MOD_ID, "effect_info", EffectInfoRecipe.class);

    protected EffectInfoRecipe(MobEffectInstance effectInstance, List<FormattedText> description) {
        super(effectInstance, description);
    }

    @Override
    public List<ItemStack> getInputItems() {
        IIngredientVisibility ingredientVisibility = JEIPlugin.JEI_INGREDIENT_VISIBILITY;
        return inputItems.stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> ingredientVisibility.isIngredientVisible(VanillaTypes.ITEM_STACK, s))
                .toList();
    }

}