package net.mehvahdjukaar.jeed.plugin.rei.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class EffectBox extends WidgetWithBounds {

    private static final ResourceLocation resource = ContainerScreen.INVENTORY_LOCATION;

    private final Rectangle bounds;

    public EffectBox(Rectangle bounds) {
        this.bounds = new Rectangle(Objects.requireNonNull(bounds));
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resource);

        GuiComponent.blit(poseStack, bounds.x, bounds.y, bounds.width, bounds.height, 141f, 166f, 24, 24, 256, 256);
        RenderSystem.applyModelViewMatrix();
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
