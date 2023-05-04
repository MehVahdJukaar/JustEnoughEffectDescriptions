package net.mehvahdjukaar.jeed.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.mehvahdjukaar.jeed.PaintingCategory;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaintingRecipeCategory extends PaintingCategory implements DisplayCategory<PaintingInfoDisplay> {

    public PaintingRecipeCategory() {
        super();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.PAINTING);
    }


    @Override
    public int getDisplayWidth(PaintingInfoDisplay display) {
        return RECIPE_WIDTH - 4;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_HEIGHT;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public CategoryIdentifier<? extends PaintingInfoDisplay> getCategoryIdentifier() {
        return JeppReiPlugin.PAINTING_INFO_TYPE;
    }

    @Override
    public List<Widget> setupDisplay(PaintingInfoDisplay display, Rectangle bounds) {
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(new PaintingWidget(bounds, display.getPainting()));


        MutableComponent name = (MutableComponent) display.getName();
        name.setStyle(Style.EMPTY.withBold(true));
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.getY() + 6), name));

        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.getMaxY() - 8 - 6), display.getDescription())
                .noShadow()
                .color(0xFF404040, 0xFFBBBBBB));

        return widgets;

    }

    private static class PaintingWidget extends WidgetWithBounds {

        private final Rectangle bounds;
        private final PaintingVariant painting;

        public PaintingWidget(Rectangle bounds, PaintingVariant paintingVariant) {
            this.bounds = new Rectangle(Objects.requireNonNull(bounds));
            this.painting = paintingVariant;
        }

        @Override
        public void render(PoseStack poseStack, int i, int j, float f) {
            //render painting
            poseStack.pushPose();
            poseStack.translate(bounds.getCenterX(), bounds.getCenterY(), 0);

            renderPainting(painting, poseStack, RECIPE_WIDTH - 14, RECIPE_HEIGHT - 14);
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