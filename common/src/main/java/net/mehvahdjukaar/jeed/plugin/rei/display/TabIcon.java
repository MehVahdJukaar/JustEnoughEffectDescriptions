package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TabIcon implements Renderer {

    private static final Identifier resource = Jeed.res("textures/gui/effects.png");

    public TabIcon() {
    }

    @Override
    public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        graphics.pose().translate(0.5f, 0);
        graphics.blit(RenderPipelines.GUI_TEXTURED, resource, bounds.x, bounds.y, 0, 0,
                bounds.width - 1, bounds.height, 15, 16);
    }
}
