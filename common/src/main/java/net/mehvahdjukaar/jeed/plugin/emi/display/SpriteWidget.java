// EMI has no 26.1 build yet - its newest release still targets 1.21.1, so there is no API to compile against.
// Kept here, commented out, so the plugin can be revived as soon as EMI updates. Still written against the 1.21 APIs.
//package net.mehvahdjukaar.jeed.plugin.emi.display;
//
//import dev.emi.emi.api.render.EmiRenderable;
//import dev.emi.emi.api.widget.Bounds;
//import dev.emi.emi.api.widget.Widget;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.resources.ResourceLocation;
//
//public final class SpriteWidget extends Widget implements EmiRenderable {
//    private final ResourceLocation sprite;
//    private final int x;
//    private final int y;
//    private final int width;
//    private final int height;
//
//    public SpriteWidget(ResourceLocation sprite, int x, int y, int width,
//                        int height) {
//        this.sprite = sprite;
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//    }
//
//    @Override
//    public Bounds getBounds() {
//        return new Bounds(this.x, this.y, this.width, this.height);
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
//        guiGraphics.blitSprite(sprite, x, y, width, height);
//    }
//}
