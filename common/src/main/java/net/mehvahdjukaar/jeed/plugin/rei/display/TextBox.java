package net.mehvahdjukaar.jeed.plugin.rei.display;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.plugin.client.categories.DefaultInformationCategory;
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TextBox extends Widget {

    private static final ResourceLocation resource = ContainerScreen.INVENTORY_LOCATION;

    private final Point left;
    private final List<FormattedText> lines;

    public TextBox(Point center, List<FormattedText> lines) {
        this.left = new Point(Objects.requireNonNull(center));
        this.lines = lines;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        int y = 0;
        for (FormattedText descriptionLine : lines) {
            font.draw(poseStack, Language.getInstance().getVisualOrder(descriptionLine), left.x, left.y + y, 0xFF000000);
            y += font.lineHeight + EffectCategory.LINE_SPACING;
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
