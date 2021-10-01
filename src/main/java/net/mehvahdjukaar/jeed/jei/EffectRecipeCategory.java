package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.utils.HSLColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
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
    private final Component localizedName;

    public EffectRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
        this.effectBackground = new EffectBox();// guiHelper.createDrawable(ContainerScreen.INVENTORY_LOCATION, 141, 166, 24, 24);

        this.icon = ICON;
        this.slotBackground = guiHelper.getSlotDrawable();
        //this.jeiPlugin = jeiPlugin;
        this.localizedName = new TranslatableComponent("jeed.category.effect_info");
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
    public Component getTitle() {
        return this.localizedName;
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
        ingredients.setInputs(VanillaTypes.ITEM, recipe.getInputItems());
        ingredients.setOutput(recipe.getEffectIngredientType(), recipe.getEffect());
    }

    @Override
    public void draw(EffectInfoRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        int xPos = 0;
        int yPos = effectBackground.getHeight() + 4 + yOffset;

        Font font = Minecraft.getInstance().font;

        MobEffect effect = recipe.getEffect().getEffect();


        BaseComponent name = (BaseComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());

        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));
        float x = recipeWidth / 2f - font.width(name) / 2f;
        font.drawShadow(matrixStack, Language.getInstance().getVisualOrder(name), x, 0, 0xFF000000);

        for (FormattedText descriptionLine : recipe.getDescription()) {
            font.draw(matrixStack, Language.getInstance().getVisualOrder(descriptionLine), xPos, yPos, 0xFF000000);
            yPos += font.lineHeight + lineSpacing;
        }

        for (int slotId = 0; slotId < 14; slotId++) {
            this.slotBackground.draw(matrixStack, (int) (recipeWidth / 2 + (19f * ((slotId % 7) - 7 / 2f))), recipeHeight - 19 * (1 + slotId / 7));
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EffectInfoRecipe recipe, IIngredients ingredients) {

        IGuiIngredientGroup<MobEffectInstance> guiEffectInstances = recipeLayout.getIngredientsGroup(JEIPlugin.EFFECT);
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        boolean box = Jeed.EFFECT_BOX.get();
        int offset = box ? 3 : 0;

        int xPos = (recipeWidth - 18) / 2;
        guiEffectInstances.init(0, true, EffectInstanceRenderer.INSTANCE_SLOT, xPos - offset, yOffset + 3 -offset, 18 +offset*2, 18+offset*2, offset, offset);
        if(box) {
            guiEffectInstances.setBackground(0, effectBackground);
        }
        guiEffectInstances.set(0, recipe.getEffect());



        List<List<ItemStack>> slotContents = Arrays.asList(NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create());
        List<ItemStack> compatible = recipe.getInputItems();

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
