package net.mehvahdjukaar.jeed.plugin.rei.display;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.scroll.ScrollingContainer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.CloseableScissors;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.plugin.client.categories.DefaultInformationCategory;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScrollableTextWidget extends WidgetWithBounds {
    private final Rectangle bounds;
    private final List<FormattedCharSequence> texts;
    private final ScrollingContainer scrolling = new ScrollingContainer() {
        @Override
        public Rectangle getBounds() {
            Rectangle bounds = ScrollableTextWidget.this.getBounds();
            return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
        }

        @Override
        public int getMaxScrollHeight() {
            int i = 2;
            for (FormattedCharSequence entry : texts) {
                i += entry == null ? 4 : font.lineHeight;
            }
            return i;
        }
    };

    public ScrollableTextWidget(Rectangle bounds, List<Component> texts) {
        this.bounds = Objects.requireNonNull(bounds);
        this.texts = Lists.newArrayList();
        for (FormattedText text : texts) {
            if (!this.texts.isEmpty())
                this.texts.add(null);
            this.texts.addAll(Minecraft.getInstance().font.split(text, bounds.width - 11));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (containsMouse(mouseX, mouseY)) {
            scrolling.offset(ClothConfigInitializer.getScrollStep() * -amount, true);
            return true;
        }
        return false;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrolling.updateDraggingState(mouseX, mouseY, button))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrolling.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {

    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }


    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        scrolling.updatePosition(delta);
        Rectangle innerBounds = scrolling.getScissorBounds();
        try (CloseableScissors scissors = scissor(matrices, innerBounds)) {
            int currentY = -scrolling.scrollAmountInt() + innerBounds.y;
            for (FormattedCharSequence text : texts) {
                if (text != null && currentY + font.lineHeight >= innerBounds.y && currentY <= innerBounds.getMaxY()) {
                    font.draw(matrices, text, innerBounds.x + 2, currentY + 2, REIRuntime.getInstance().isDarkThemeEnabled() ? 0xFFBBBBBB : 0xFF090909);
                }
                currentY += text == null ? 4 : font.lineHeight;
            }
        }
        if (scrolling.hasScrollBar()) {
            if (scrolling.scrollAmount() > 8) {
                fillGradient(matrices, innerBounds.x, innerBounds.y, innerBounds.getMaxX(), innerBounds.y + 16, 0xFFC6C6C6, 0x00C6C6C6);
            }
            if (scrolling.getMaxScroll() - scrolling.scrollAmount() > 8) {
                fillGradient(matrices, innerBounds.x, innerBounds.getMaxY() - 16, innerBounds.getMaxX(), innerBounds.getMaxY(), 0x00C6C6C6, 0xFFC6C6C6);
            }
        }
        try (CloseableScissors scissors = scissor(matrices, scrolling.getBounds())) {
            scrolling.renderScrollBar(0, 1, 1f);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
}