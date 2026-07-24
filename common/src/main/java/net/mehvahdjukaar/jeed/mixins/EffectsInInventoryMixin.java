package net.mehvahdjukaar.jeed.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Loader agnostic part. The tooltip suppression lives in the per loader mixins since NeoForge patches
 * {@code extractText} into a stub that delegates to its own {@code renderText}.
 */
@Mixin(EffectsInInventory.class)
public abstract class EffectsInInventoryMixin {

    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

    @Unique
    private int jeed$mouseX, jeed$mouseY;

    @Inject(at = @At("HEAD"), method = "extractRenderState")
    private void jeed$captureMouse(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo info) {
        jeed$mouseX = mouseX;
        jeed$mouseY = mouseY;
        NativeCompat.setInventoryEffect(null, false);
    }

    @WrapOperation(method = "extractEffects",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")
    )
    private void jeed$captureHoveredEffect(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite,
                                           int px, int py, int spriteWidth, int spriteHeight, Operation<Void> original,
                                           @Local MobEffectInstance hoveredEffect) {
        original.call(graphics, pipeline, sprite, px, py, spriteWidth, spriteHeight);

        //the icon is drawn 7px inside the effect background
        int x = px - 7;
        int y = py - 7;
        boolean big = jeed$hasRoomForText();
        int width = big ? 120 : 32;
        int height = 32;

        if (jeed$mouseX >= x && jeed$mouseX <= x + width && jeed$mouseY >= y && jeed$mouseY <= y + height) {
            NativeCompat.setInventoryEffect(hoveredEffect, !big);
        }
    }

    /**
     * Mirrors the {@code availableWidth >= 120} check vanilla uses to decide between the wide and the icon-only layout.
     */
    @Unique
    private boolean jeed$hasRoomForText() {
        int xo = this.screen.leftPos + this.screen.imageWidth + 2;
        return this.screen.width - xo >= 120;
    }
}