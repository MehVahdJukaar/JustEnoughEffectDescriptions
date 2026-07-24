package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.mehvahdjukaar.jeed.common.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TextBox extends Widget {

    private final Point left;
    private final List<FormattedText> lines;

    public TextBox(Point center, List<FormattedText> lines) {
        this.left = new Point(Objects.requireNonNull(center));
        this.lines = lines;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int y = 0;
        for (FormattedText descriptionLine : lines) {
            graphics.drawString(font, Language.getInstance().getVisualOrder(descriptionLine), left.x, left.y + y, 0xFF000000, false);
            y += font.lineHeight + Constants.LINE_SPACING;
        }
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
}
