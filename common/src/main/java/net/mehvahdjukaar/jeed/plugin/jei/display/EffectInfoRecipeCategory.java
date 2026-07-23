package net.mehvahdjukaar.jeed.plugin.jei.display;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public class EffectInfoRecipeCategory implements IRecipeCategory<EffectInfoRecipe> {

    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable effectBackground;

    public EffectInfoRecipeCategory(IGuiHelper guiHelper) {
        this.effectBackground = new EffectBox();
        this.icon = new TabIcon();
        this.slotBackground = guiHelper.getSlotDrawable();
    }

    @Override
    public IRecipeType<EffectInfoRecipe> getRecipeType() {
        return EffectInfoRecipe.TYPE;
    }

    @Override
    public Component getTitle() {
        return LOCALIZED_NAME;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return RECIPE_WIDTH;
    }

    @Override
    public int getHeight() {
        return RECIPE_HEIGHT;
    }

    @Override
    public void draw(EffectInfoRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        int xPos = 0;
        int yPos = effectBackground.getHeight() + 4 + Y_OFFSET;

        Font font = Minecraft.getInstance().font;

        MobEffect effect = recipe.getEffect().getEffect().value();


        MutableComponent name = (MutableComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());

        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));
        float x = RECIPE_WIDTH / 2f - font.width(name) / 2f;
        graphics.text(font, Language.getInstance().getVisualOrder(name), (int) x, 0, 0xFF000000);

        for (FormattedText descriptionLine : recipe.getDescription()) {
            graphics.text(font, Language.getInstance().getVisualOrder(descriptionLine), xPos, yPos, 0xFF000000, false);
            yPos += font.lineHeight + LINE_SPACING;
        }

        if (Jeed.hasIngredientList()) {
            int size = recipe.ingredients.size();
            if (size != 0) {
                int maxSlots = size <= SLOTS_PER_ROW ? SLOTS_PER_ROW : SLOTS_PER_ROW * ROWS;
                for (int slotId = 0; slotId < maxSlots; slotId++) {
                    int slotX = 1 + (int) (RECIPE_WIDTH / 2f + ((float) SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f)));
                    this.slotBackground.draw(graphics, slotX,
                            RECIPE_HEIGHT - SLOT_W * (1 + slotId / SLOTS_PER_ROW));
                }
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EffectInfoRecipe recipe, IFocusGroup focuses) {
        IIngredientType<MobEffectInstance> type = JEIPlugin.EFFECT_INGREDIENT_TYPE;
        //adds to both output and input
        IRecipeSlotBuilder mainSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, (RECIPE_WIDTH - 18) / 2, Y_OFFSET + 3)
                .setCustomRenderer(type, EffectInstanceRenderer.INSTANCE_SLOT)
                .add(type, recipe.getEffect());

        //hack so we have both input and outputs to make it easier to access effects using U and R keys. This one is set to not render
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .add(type, recipe.getEffect());

        if (Jeed.hasEffectBox()) {
            mainSlot.setBackground(effectBackground, -3, -3);
        }

        if (Jeed.hasIngredientList()) {

            List<List<ItemStack>> slotContents = recipe.slots;

            int rowsCount = slotContents.size() <= SLOTS_PER_ROW ? 1 : ROWS;

            for (int slotId = 0; slotId < slotContents.size(); slotId++) {
                int x = (int) ((float) RECIPE_WIDTH / 2 + (float) ROWS + (SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f)));

                int y = 1 + RECIPE_HEIGHT - SLOT_W * (rowsCount - (slotId / SLOTS_PER_ROW));
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStacks(slotContents.get(slotId));
            }
        }
    }
}
