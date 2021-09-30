package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.utils.HSLColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EffectRecipeCategory implements IRecipeCategory<EffectInfoRecipe> {

    public static final ResourceLocation UID = Jeed.res("effects");

    private static final TabIcon ICON = new TabIcon();

    public static final int recipeWidth = 160;
    public static final int recipeHeight = 125;
    private static final int lineSpacing = 2;

    private static final int yOffset = 12;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable effectBackground;
    //private final JeiInternalPlugin jeiPlugin;
    private final ITextComponent localizedName;

    public EffectRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
        this.effectBackground = new EffectBox();// guiHelper.createDrawable(ContainerScreen.INVENTORY_LOCATION, 141, 166, 24, 24);

        this.icon = ICON;
        this.slotBackground = guiHelper.getSlotDrawable();
        //this.jeiPlugin = jeiPlugin;
        this.localizedName = new TranslationTextComponent("jeed.category.effect_info");
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends EffectInfoRecipe> getRecipeClass() {
        return EffectInfoRecipe.class;
    }

    @Override
    @Deprecated
    public String getTitle() {
        return this.localizedName.getString();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setIngredients(EffectInfoRecipe recipe, IIngredients ingredients) {
        IIngredientType<EffectInstance> ingredientType = recipe.getIngredientType();
        List<List<EffectInstance>> recipeIngredients = Collections.singletonList(Collections.singletonList(recipe.getIngredient()));
        ingredients.setInputLists(ingredientType, recipeIngredients);
        ingredients.setOutputLists(ingredientType, recipeIngredients);
    }

    @Override
    public void draw(EffectInfoRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        int xPos = 0;
        int yPos = effectBackground.getHeight() + 4 + yOffset;

        FontRenderer font = Minecraft.getInstance().font;

        Effect effect = recipe.getIngredient().getEffect();


        TextComponent name = (TextComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());

        name.setStyle(Style.EMPTY.withBold(true).withColor(Color.fromRgb(color)));
        float x = recipeWidth / 2f - font.width(name) / 2f;
        font.drawShadow(matrixStack, LanguageMap.getInstance().getVisualOrder(name), x, 0, 0xFF000000);

        for (ITextProperties descriptionLine : recipe.getDescription()) {
            font.draw(matrixStack, LanguageMap.getInstance().getVisualOrder(descriptionLine), xPos, yPos, 0xFF000000);
            yPos += font.lineHeight + lineSpacing;
        }

        for (int slotId = 0; slotId < 14; slotId++) {
            this.slotBackground.draw(matrixStack, (int) (recipeWidth / 2 + (19f * ((slotId % 7) - 7 / 2f))), recipeHeight - 19 * (1 + slotId / 7));
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EffectInfoRecipe recipe, IIngredients ingredients) {

        IGuiIngredientGroup<EffectInstance> guiEffectInstances = recipeLayout.getIngredientsGroup(JEIPlugin.EFFECT);
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        boolean box = Jeed.EFFECT_BOX.get();
        int offset = box ? 3 : 0;

        int xPos = (recipeWidth - 18) / 2;
        guiEffectInstances.init(0, true, EffectInstanceRenderer.INSTANCE_SLOT, xPos - offset, yOffset + 3 -offset, 18 +offset*2, 18+offset*2, offset, offset);
        if(box) {
            guiEffectInstances.setBackground(0, effectBackground);
        }
        guiEffectInstances.set(ingredients);



        List<List<ItemStack>> slotContents = Arrays.asList(NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create());
        List<ItemStack> compatible = recipe.getEffectProviders();

        for (int slotId = 0; slotId < compatible.size(); slotId++) {

            slotContents.get(slotId % slotContents.size()).add(compatible.get(slotId));
        }

        for (int slotId = 0; slotId < slotContents.size(); slotId++) {

            stacks.init(slotId + 1, false, (int) (recipeWidth / 2 + (19f * ((slotId % 7) - 7 / 2f))), recipeHeight - 19*(2 - ( slotId / 7)));
            stacks.set(slotId + 1, slotContents.get(slotId));

            //stacks.addTooltipCallback(recipe);
        }

    }
}
