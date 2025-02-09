package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.Constants;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;

import me.shedaniel.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public class EffectInfoDisplayCategory implements DisplayCategory<EffectInfoDisplay> {

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
        return LOCALIZED_NAME;
    }

    @Override
    public CategoryIdentifier<? extends EffectInfoDisplay> getCategoryIdentifier() {
        return REIPlugin.EFFECTS_INFO_CATEGORY;
    }

    @Override
    public List<Widget> setupDisplay(EffectInfoDisplay display, me.shedaniel.math.Rectangle bounds) {
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        MobEffect effect = display.getEffect().getEffect().value();

        MutableComponent name = (MutableComponent) effect.getDisplayName();
        int color = HSLColor.getProcessedColor(effect.getColor());
        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));

        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 6), name)
                .centered());

        Rectangle rect = new Rectangle(bounds.x + (bounds.width - 18) / 2, bounds.y + Y_OFFSET + 3 + 6,
                18, 18);

        Rectangle rect2 = rect.clone();
        rect2.grow(3, 3);
        widgets.add(Widgets.createTexturedWidget(EFFECT_BACKGROUND_SMALL_TEXTURE, rect2,
                0, 0, 24, 24, 24, 24));


        widgets.add(Widgets.createSlot(rect)
                .disableBackground()
                .markInput().entry(display.getOutputEntries().get(0).get(0)));

        var slotContents = display.getSlots();
        int listH = EffectInfo.getListHeight(slotContents);

        widgets.add(new ScrollableTextWidget(new Rectangle(bounds.x + SIZE_DIFF,
                rect2.getMaxY() + 1, bounds.width - 2 * SIZE_DIFF,
                50 + Constants.MAX_BOX_HEIGHT - listH), display.getDescription()));

        if (listH != 0) {

            int rowsCount = slotContents.size() <= SLOTS_PER_ROW ? 1 : ROWS;

            boolean renderSlots = Jeed.rendersSlots();
            if (!renderSlots) {
                widgets.add(Widgets.createSlotBase(new Rectangle(bounds.x + (int) (bounds.width / 2f - (SLOT_W * SLOTS_PER_ROW) / 2f),
                        bounds.getMaxY() - SLOT_W * rowsCount - 7, SLOTS_PER_ROW * SLOT_W + 1, rowsCount * SLOT_W + 1)));
            }

            int size = renderSlots ? SLOTS_PER_ROW * (slotContents.size() <= SLOTS_PER_ROW ? 1 : ROWS) : slotContents.size();

            for (int slotId = 0; slotId < size; slotId++) {
                Slot slot = Widgets.createSlot(new Point(
                        2 + bounds.x + (int) (bounds.width / 2f - (SLOT_W * SLOTS_PER_ROW) / 2f + (SLOT_W * (slotId % SLOTS_PER_ROW))),
                        2 + bounds.getMaxY() - SLOT_W * rowsCount + SLOT_W * (slotId / SLOTS_PER_ROW) - 7));

                if (!renderSlots) slot.disableBackground();
                if (slotId < slotContents.size()) {
                    var v = slotContents.get(slotId);
                    slot.entries(v);
                }
                widgets.add(slot);
            }
        }

        return widgets;

    }

}