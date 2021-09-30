package net.mehvahdjukaar.jeed.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.drawable.IDrawable;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class TabIcon implements IDrawable {

    private static final ResourceLocation resource = Jeed.res("textures/gui/effects.png");

    private final int width, height;

    public TabIcon() {
        this.width = 15;
        this.height = 16;
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
        AbstractGui.blit(matrixStack, xOffset, yOffset, 0, 0, 0, width, height, width, height);
        //GuiUtils.drawTexturedModalRect(xOffset + 0, yOffset + 0, 0, 0, this.width*2, this.height, 0);
        RenderSystem.popMatrix();
    }

}
