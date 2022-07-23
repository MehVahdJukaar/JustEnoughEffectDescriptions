package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.utils.HSLColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EffectRecipeCategory implements IRecipeCategory<EffectInfoRecipe> {

    public static final RecipeType<EffectInfoRecipe> TYPE = RecipeType.create(Jeed.MOD_ID, "effects", EffectInfoRecipe.class);

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
        this.effectBackground = new EffectBox(); // guiHelper.createDrawable(ContainerScreen.INVENTORY_LOCATION, 141, 166, 24, 24);

        this.icon = ICON;
        this.slotBackground = guiHelper.getSlotDrawable();
        //this.jeiPlugin = jeiPlugin;
        this.localizedName = Component.translatable("jeed.category.effect_info");
    }


    @Override
    public RecipeType<EffectInfoRecipe> getRecipeType() {
        return TYPE;
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
    public void draw(EffectInfoRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        int xPos = 0;
        int yPos = effectBackground.getHeight() + 4 + yOffset;

        Font font = Minecraft.getInstance().font;

        MobEffect effect = recipe.getEffect().getEffect();


        MutableComponent name = (MutableComponent) effect.getDisplayName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, EffectInfoRecipe recipe, IFocusGroup focuses) {
        IIngredientType<MobEffectInstance> type = recipe.getEffectIngredientType();
        //adds to both output and input
        IRecipeSlotBuilder mainSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, (recipeWidth - 18) / 2, yOffset + 3)
                .setCustomRenderer(type, EffectInstanceRenderer.INSTANCE_SLOT)
                .addIngredient(type, recipe.getEffect());

        //hack so we have both input and outputs to make it easier to access effects using U and R keys. This one is set to not render
        IRecipeSlotBuilder second = builder.addSlot(RecipeIngredientRole.INPUT, 1 + (recipeWidth - 18) / 2, 1 + yOffset + 3)
               // .setCustomRenderer(JEIPlugin.EFFECT, (effectInstance, tooltipFlag) -> List.of())
                .addIngredient(type, recipe.getEffect());

        if (Jeed.EFFECT_BOX.get()) {
            mainSlot.setBackground(effectBackground, -3, -3);
        }

        List<List<ItemStack>> slotContents = Arrays.asList(NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create(), NonNullList.create());
        List<ItemStack> compatible = recipe.getInputItems();

        for (int slotId = 0; slotId < compatible.size(); slotId++) {
            slotContents.get(slotId % slotContents.size()).add(compatible.get(slotId));
        }

        for (int slotId = 0; slotId < slotContents.size(); slotId++) {
            int x = 1 + (int) (recipeWidth / 2 + (19f * ((slotId % 7) - 7 / 2f)));
            int y = 1 + recipeHeight - 19 * (2 - (slotId / 7));
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStacks(slotContents.get(slotId));
        }
    }
}
