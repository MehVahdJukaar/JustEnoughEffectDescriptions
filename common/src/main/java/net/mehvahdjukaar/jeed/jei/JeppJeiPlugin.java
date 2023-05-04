package net.mehvahdjukaar.jeed.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mehvahdjukaar.jeed.Jepp;
import net.mehvahdjukaar.jeed.PaintingInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@JeiPlugin
public class JeppJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = Jepp.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    public static final RecipeType<PaintingInfo> PAINTING_INFO_TYPE =
            RecipeType.create(Jepp.MOD_ID, "painting", PaintingInfo.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new PaintingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        for (Holder<PaintingVariant> painting : Registry.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE)) {
            PaintingInfo recipe = new PaintingInfo(painting.value());
            registry.addRecipes(PAINTING_INFO_TYPE, List.of(recipe));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.PAINTING), PAINTING_INFO_TYPE);
    }


}
