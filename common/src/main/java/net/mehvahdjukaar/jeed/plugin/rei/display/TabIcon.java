package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TabIcon implements Renderer {

    private static final ResourceLocation resource = Jeed.res("textures/gui/effects.png");

    public TabIcon() {
    }

    @Override
    public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        graphics.pose().translate(0.5, 0, 0);
        graphics.blit(resource, bounds.x, bounds.y, 0, 0, 0, bounds.width - 1, bounds.height, 15, 16);
    }

}
