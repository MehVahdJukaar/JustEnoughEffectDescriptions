package net.mehvahdjukaar.jeed.plugin.rei.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import mezz.jei.api.gui.drawable.IDrawable;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class TabIcon implements Renderer {

    private static final ResourceLocation resource = Jeed.res("textures/gui/effects.png");

    private int z;

    public TabIcon() {
    }

    @Override
    public void render(PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {

        RenderSystem.clearColor(1.0F, 1.0F,1.0F,1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resource);
        matrices.translate(0.5,0,0);
        GuiComponent.blit(matrices, bounds.x, bounds.y, z, 0, 0, bounds.width-1, bounds.height, 15, 16);

        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
    }
}
