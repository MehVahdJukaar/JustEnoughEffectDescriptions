package net.mehvahdjukaar.jeed.plugin.rei.display;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EffectInfoDisplayCategory extends EffectCategory implements DisplayCategory<EffectInfoDisplay> {

    public static final int SIZE_DIFF = 3;

    private final Renderer icon = new TabIcon();

    public EffectInfoDisplayCategory() {
        super();
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(EffectInfoDisplay display) {
        return RECIPE_WIDTH - 4;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_HEIGHT + 14;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public CategoryIdentifier<? extends EffectInfoDisplay> getCategoryIdentifier() {
        return REIPlugin.EFFECTS_INFO_CATEGORY;
    }

    @Override
    public List<Widget> setupDisplay(EffectInfoDisplay display, Rectangle bounds) {
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        MobEffect effect = display.getEffect().getEffect();

        MutableComponent name = (MutableComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());
        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));

        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 6), name)
                .centered());

        Rectangle rect = new Rectangle(bounds.x + (bounds.width - 18) / 2, bounds.y + Y_OFFSET + 3 + 6,
                18, 18);


        Rectangle rect2 = rect.clone();
        rect2.grow(3, 3);
        widgets.add(Widgets.createTexturedWidget(ContainerScreen.INVENTORY_LOCATION, rect2,
                141f, 166f, 24, 24, 256, 256));

        widgets.add(Widgets.createSlot(rect)
                .disableBackground()
                .markInput().entry(display.getOutputEntries().get(0).get(0)));

        List<ItemStack> inputItems = display.getInputItems();
        int listH = EffectInfo.getListHeight(inputItems);

        widgets.add(new ScrollableTextWidget(new Rectangle(bounds.x + SIZE_DIFF,
                rect2.getMaxY() + 1, bounds.width - 2 * SIZE_DIFF,
                50 + EffectCategory.EMPTY_LIST_EXTRA_HEIGHT - listH), display.getComponents()));

        if (listH != 0) {


            List<List<EntryStack<?>>> slotContents = new ArrayList<>();

            for (int slotId = 0; slotId < inputItems.size(); slotId++) {

                int ind = slotId % (SLOTS_PER_ROW * ROWS);
                if (slotContents.size() <= ind) slotContents.add(new ArrayList<>());
                slotContents.get(ind).add(EntryStacks.of((inputItems.get(slotId))));
            }

            int r = slotContents.size() <= SLOTS_PER_ROW ? 1 : ROWS;

            widgets.add(Widgets.createSlotBase(new Rectangle(bounds.x + (int) (bounds.width / 2f - (SLOT_W * SLOTS_PER_ROW) / 2f),
                    bounds.getMaxY() - SLOT_W * r - 7, SLOTS_PER_ROW * SLOT_W + 1, r * SLOT_W + 1)));


            for (int slotId = 0; slotId < slotContents.size(); slotId++) {
                var v = slotContents.get(slotId);
                if (v == null) break;
                widgets.add(Widgets.createSlot(new Point(
                                2 + bounds.x + (int) (bounds.width / 2f - (SLOT_W * SLOTS_PER_ROW) / 2f + (SLOT_W * (slotId % SLOTS_PER_ROW))),
                                2 + bounds.getMaxY() - SLOT_W * r + SLOT_W * (slotId / SLOTS_PER_ROW) - 7))
                        .disableBackground()
                        .entries(v));
            }
        }

        return widgets;

    }

    private static class EffectWidget extends WidgetWithBounds {

        private final Rectangle bounds;
        private final PaintingVariant painting;

        public EffectWidget(Rectangle bounds, PaintingVariant paintingVariant) {
            this.bounds = new Rectangle(Objects.requireNonNull(bounds));
            this.painting = paintingVariant;
        }

        @Override
        public void render(PoseStack poseStack, int i, int j, float f) {
            //render painting
            poseStack.pushPose();
            poseStack.translate(bounds.getCenterX(), bounds.getCenterY(), 0);

            poseStack.popPose();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public boolean isDragging() {
            return false;
        }

        @Override
        public void setDragging(boolean isDragging) {
        }

        @Nullable
        @Override
        public GuiEventListener getFocused() {
            return null;
        }

        @Override
        public void setFocused(@Nullable GuiEventListener focused) {
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }
    }


}