package net.mehvahdjukaar.jeed.plugin.jei.display;

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
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EffectRecipeCategory extends EffectCategory implements IRecipeCategory<EffectInfoRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable effectBackground;

    public EffectRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(RECIPE_WIDTH, RECIPE_HEIGHT);
        this.effectBackground = new EffectBox(); // guiHelper.createDrawable(ContainerScreen.INVENTORY_LOCATION, 141, 166, 24, 24);

        this.icon = new TabIcon();
        this.slotBackground = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<EffectInfoRecipe> getRecipeType() {
        return EffectInfoRecipe.TYPE;
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
        int yPos = effectBackground.getHeight() + 4 + Y_OFFSET;

        Font font = Minecraft.getInstance().font;

        MobEffect effect = recipe.getEffect().getEffect();


        MutableComponent name = (MutableComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());

        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));
        float x = RECIPE_WIDTH / 2f - font.width(name) / 2f;
        font.drawShadow(matrixStack, Language.getInstance().getVisualOrder(name), x, 0, 0xFF000000);

        for (FormattedText descriptionLine : recipe.getDescription()) {
            font.draw(matrixStack, Language.getInstance().getVisualOrder(descriptionLine), xPos, yPos, 0xFF000000);
            yPos += font.lineHeight + LINE_SPACING;
        }

        if (Jeed.hasIngredientList()) {
            int size = recipe.getInputItems().size();
            if (size != 0) {
                int maxSlots = size <= SLOTS_PER_ROW ? SLOTS_PER_ROW : SLOTS_PER_ROW * ROWS;
                for (int slotId = 0; slotId < maxSlots; slotId++) {
                    this.slotBackground.draw(matrixStack, 1+(int) (RECIPE_WIDTH / 2f + ((float) SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f))),
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
                .addIngredient(type, recipe.getEffect());

        //hack so we have both input and outputs to make it easier to access effects using U and R keys. This one is set to not render
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                // .setCustomRenderer(JEIPlugin.EFFECT, (effectInstance, tooltipFlag) -> List.of())
                .addIngredient(type, recipe.getEffect());

        if (Jeed.hasEffectBox()) {
            mainSlot.setBackground(effectBackground, -3, -3);
        }

        if (Jeed.hasIngredientList()) {
            List<ItemStack> inputItems = recipe.getInputItems();

            List<List<ItemStack>> slotContents = new ArrayList<>();

            for (int slotId = 0; slotId < inputItems.size(); slotId++) {

                int ind = slotId % (SLOTS_PER_ROW * ROWS);
                if (slotContents.size() <= ind) slotContents.add(new ArrayList<>());
                slotContents.get(ind).add(inputItems.get(slotId));
            }

            int r = inputItems.size() <= SLOTS_PER_ROW ? 1 : ROWS;


            for (int slotId = 0; slotId < slotContents.size(); slotId++) {
                int x = 2 + (int) (RECIPE_WIDTH / (float) ROWS + (SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f)));
                int y = 1 + RECIPE_HEIGHT - SLOT_W * (r - (slotId / SLOTS_PER_ROW));
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addItemStacks(slotContents.get(slotId));
            }
        }
    }
}
