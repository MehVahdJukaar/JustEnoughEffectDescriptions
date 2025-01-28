package net.mehvahdjukaar.jeed.plugin.emi.display;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.runtime.EmiDrawContext;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectWindowEntry;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.emi.EMIPlugin;
import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectInstanceStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public class EffectInfoRecipe extends EffectWindowEntry implements EmiRecipe {

    private final ResourceLocation id;
    private final List<EmiIngredient> catalysts;
    private final List<EmiIngredient> slotsContent;
    private final EmiStack outputs;

    protected EffectInfoRecipe(MobEffectInstance effectInstance, Component description, ResourceLocation id) {
        super(effectInstance, List.of(description));
        this.id = id;
        this.outputs = new EffectInstanceStack(effectInstance);
        var providers = computeItemProviders(effectInstance.getEffect());
        var ingredientsList = groupIngredients(providers)
                .stream().map(EmiIngredient::of).toList();
        this.catalysts = providers.stream().map(Ingredient::of).map(EmiIngredient::of).toList();
        ingredientsList = new ArrayList<>(ingredientsList);
        this.inputEffects = computeEffectProviders(effect).stream()
                .map(EffectInstanceStack::new).map(e -> ((EmiIngredient) e)).toList();

        List<EmiIngredient> fluids = computeFluidProvides(effect).stream()
                .map(f -> EmiStack.of(f.value())).map(e -> ((EmiIngredient) e)).toList();
        ingredientsList.addAll(inputEffects);
        ingredientsList.addAll(fluids);
        this.slotsContent = divideIntoSlots(ingredientsList, EmiIngredient::of);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIPlugin.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(outputs);
    }

    @Override
    public int getDisplayWidth() {
        return RECIPE_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_HEIGHT;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        MobEffect mobEffect = effect.getEffect();

        MutableComponent name = (MutableComponent) mobEffect.getDisplayName();
        int color = HSLColor.getProcessedColor(mobEffect.getColor());
        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));

        Font font = Minecraft.getInstance().font;
        int centerX = widgets.getWidth() / 2;
        int nameX = (int) (centerX - font.width(name) / 2f);
        widgets.addText(name, nameX, 0, -1, true);

        if (Jeed.hasEffectBox()) {
            widgets.addTexture(ContainerScreen.INVENTORY_LOCATION, centerX - 12, Y_OFFSET,
                    24, 24, 141, 166);
        }

        widgets.add(new SlotWidget(outputs, centerX - 9, Y_OFFSET + 3)
                .drawBack(false));


        int listH = EffectWindowEntry.getListHeight(slotsContent);

        if (listH != 0) {

            int rowsCount = slotsContent.size() <= SLOTS_PER_ROW ? 1 : ROWS;
            int size = SLOTS_PER_ROW * (slotsContent.size() <= SLOTS_PER_ROW ? 1 : ROWS);

            for (int slotId = 0; slotId < size; slotId++) {
                EmiIngredient ingredient;
                if (slotId < slotsContent.size()) {
                    ingredient = slotsContent.get(slotId);
                } else ingredient = EmiStack.EMPTY;

                int sx = -1 + (int) ((float) centerX + (float) ROWS + (SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f)));
                int sy = 1 + widgets.getHeight() - SLOT_W * (rowsCount - (slotId / SLOTS_PER_ROW));

                SlotWidget slot = new SlotWidget(ingredient, sx, sy) {
                    @Override
                    public void drawSlotHighlight(GuiGraphics draw, Bounds bounds) {
                        if (!ingredient.isEmpty()) super.drawSlotHighlight(draw, bounds);
                    }
                };
                widgets.add(slot);
            }
        }


        int y = 2 * 18 + 4 + 1;
        int lineCount = (widgets.getHeight() - y - listH) / font.lineHeight;
        var lines = description.stream().flatMap(t -> font.split(t, getDisplayWidth() - 4).stream()).toList();
        PageManager manager = new PageManager(lines, lineCount);
        if (lineCount < lines.size()) {
            widgets.addButton(2, 2, 12, 12, 0, 0, () -> true, (mouseX, mouseY, button) -> {
                manager.scroll(-1);
            });
            widgets.addButton(widgets.getWidth() - 14, 2, 12, 12, 12, 0, () -> true, (mouseX, mouseY, button) -> {
                manager.scroll(1);
            });
        }
        widgets.addDrawable(0, y, 0, 0, (raw, mouseX, mouseY, delta) -> {
            EmiDrawContext context = EmiDrawContext.wrap(raw);
            int lo = manager.start();
            for (int i = 0; i < lineCount; i++) {
                int l = lo + i;
                if (l >= manager.lines.size()) {
                    return;
                }
                FormattedCharSequence text = manager.lines.get(l);
                context.drawText(text, 0, y - y + i * font.lineHeight, 0);
            }
        });

    }

    public static EffectInfoRecipe create(MobEffect effect) {
        Component text = getDescription(effect);

        return new EffectInfoRecipe(new MobEffectInstance(effect), text, BuiltInRegistries.MOB_EFFECT.getKey(effect));
    }

    private static class PageManager {
        public final List<FormattedCharSequence> lines;
        public final int pageSize;
        public int currentPage;

        public PageManager(List<FormattedCharSequence> lines, int pageSize) {
            this.lines = lines;
            this.pageSize = pageSize;
        }

        public void scroll(int delta) {
            currentPage += delta;
            int totalPages = (lines.size() - 1) / pageSize + 1;
            if (currentPage < 0) {
                currentPage = totalPages - 1;
            }
            if (currentPage >= totalPages) {
                currentPage = 0;
            }
        }

        public int start() {
            return currentPage * pageSize;
        }
    }

}
