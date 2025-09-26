package net.mehvahdjukaar.jeed.plugin.jei.display;

import mezz.jei.api.gui.drawable.IDrawable;
import net.mehvahdjukaar.jeed.common.Constants;
import net.minecraft.client.gui.GuiGraphics;

public class EffectBox implements IDrawable {

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
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        graphics.blitSprite(Constants.EFFECT_BACKGROUND_SMALL_SPRITE, xOffset, yOffset, width, height);
    }

}
