package net.mehvahdjukaar.jeed.jei.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class EffectBox implements IDrawable {

    private static final ResourceLocation resource = ContainerScreen.INVENTORY_LOCATION;

    private final int width, height;

    public EffectBox() {
        this.width = 24;
        this.height = 24;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {

        RenderSystem.clearColor(1.0F, 1.0F,1.0F,1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resource);

        GuiComponent.blit(matrixStack, xOffset, yOffset, 0, 141f, 166f, width, height, 256, 256);
        //GuiUtils.drawTexturedModalRect(poseStack, xOffset + PADDING, yOffset + PADDING, 0, 0, this.width, this.height, 0);
        RenderSystem.applyModelViewMatrix();
    }

}
