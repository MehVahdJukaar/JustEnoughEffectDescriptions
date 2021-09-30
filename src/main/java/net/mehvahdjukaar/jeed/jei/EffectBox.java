package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;

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
    public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
        RenderSystem.clearCurrentColor();
        Minecraft.getInstance().getTextureManager().bind(resource);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.last().pose());
        AbstractGui.blit(matrixStack, xOffset, yOffset, 0, 141f, 166f, width, height, 256, 256);
        RenderSystem.popMatrix();
    }

}
