package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mehvahdjukaar.jeed.PaintingCategory;
import net.mehvahdjukaar.jeed.PaintingInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PaintingRecipeCategory extends PaintingCategory implements IRecipeCategory<PaintingInfo> {


    private final IDrawable background;
    private final IDrawable icon;

    public PaintingRecipeCategory(IGuiHelper guiHelper) {
        super();
        this.background = guiHelper.createBlankDrawable(RECIPE_WIDTH, RECIPE_HEIGHT);

        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAINTING));
    }

    @Override
    public RecipeType<PaintingInfo> getRecipeType() {
        return JeppJeiPlugin.PAINTING_INFO_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PaintingInfo paintingInfoRecipe, IFocusGroup iFocusGroup) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAINTING));
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAINTING));
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.PAINTING));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(PaintingInfo recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {

        poseStack.pushPose();

        Font font = Minecraft.getInstance().font;

        MutableComponent name = (MutableComponent) recipe.getName();
        name.setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.WHITE));
        float centerX = RECIPE_WIDTH / 2f - font.width(name) / 2f;
        font.draw(poseStack, Language.getInstance().getVisualOrder(name), centerX, 0, 0xFFFFFFFF);

        FormattedText descriptionLine = recipe.getDescription();
        centerX = RECIPE_WIDTH / 2f - font.width(descriptionLine) / 2f;
        font.draw(poseStack, Language.getInstance().getVisualOrder(descriptionLine), centerX, RECIPE_HEIGHT - 8, 0xFF404040);

        poseStack.translate(RECIPE_WIDTH / 2f, RECIPE_HEIGHT / 2f, 0);
        renderPainting(recipe.getPainting(), poseStack, RECIPE_WIDTH, RECIPE_HEIGHT);

        poseStack.popPose();
    }


}
