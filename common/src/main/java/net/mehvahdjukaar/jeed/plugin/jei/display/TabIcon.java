package net.mehvahdjukaar.jeed.plugin.jei.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

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
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        graphics.blit(resource, xOffset, yOffset, 0, 0, 0, width, height, width, height);
    }

}
