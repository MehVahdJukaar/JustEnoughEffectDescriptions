package net.mehvahdjukaar.jeed;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public abstract class PaintingCategory {

    public static final int RECIPE_WIDTH = 160;
    public static final int RECIPE_HEIGHT = 125;
    protected final MutableComponent localizedName;

    protected PaintingCategory() {
        this.localizedName = Component.translatable("jepp.category.paintings_info");
    }

    protected static void renderPainting(PaintingVariant motive, PoseStack poseStack, int width, int height) {
        //render painting
        float spacing = 12;
        float maxWidth = width;
        float maxHeight = height - spacing - 12;


        int pWidth = motive.getWidth();
        int pHeight = motive.getHeight();

        float ratio = pHeight / (float) pWidth;
        float screenRatio = maxHeight / maxWidth;

        float scale = ratio < screenRatio ? maxWidth / pWidth : maxHeight / pHeight;


        poseStack.scale(scale, scale, scale);

        ResourceLocation texture = Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location();
        PaintingTextureManager paintingtexturemanager = Minecraft.getInstance().getPaintingTextures();
        TextureAtlasSprite sprite = paintingtexturemanager.get(motive);

        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);


        poseStack.translate(-pWidth / 2f, -pHeight / 2f, 0);
        GuiComponent.blit(poseStack, 0, 0, 0, pWidth, pHeight, sprite);

        RenderSystem.applyModelViewMatrix();
    }
}
