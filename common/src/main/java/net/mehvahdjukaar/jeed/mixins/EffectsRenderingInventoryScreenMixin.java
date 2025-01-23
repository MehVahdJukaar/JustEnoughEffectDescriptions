package net.mehvahdjukaar.jeed.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.joml.Matrix4f;
import org.joml.Vector4i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectsRenderingInventoryScreenMixin {

    @Inject(at = @At("HEAD"), method = "renderEffects")
    private void captureMouse(GuiGraphics matrices, int mouseX, int mouseY, CallbackInfo info) {
        jeed$mouseX = mouseX;
        jeed$mouseY = mouseY;
        NativeCompat.setInventoryEffect(null, false);
    }

    @Unique
    private int jeed$mouseX, jeed$mouseY;

    @WrapOperation(method = "renderIcons",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V")
    )
    private void jeed$captureHoveredEffect(GuiGraphics instance, int px, int py, int blitOffset, int pwidth, int pheight,
                                      TextureAtlasSprite sprite, Operation<Void> original,
                                      @Local(argsOnly = true) GuiGraphics graphics,
                                      @Local(argsOnly = true) boolean isSmall,
                                      @Local MobEffectInstance hoveredEffect) {
        original.call(instance, px, py, blitOffset, pwidth, pheight, sprite);

        if (hoveredEffect != null) {
            Matrix4f last = graphics.pose().last().pose();
            Vector4i vec = new Vector4i(px - (isSmall ? 6 : 7), py - 7, 0, 1);
            last.mul(last);
            int x = vec.x();
            int y = vec.y();
            int width = isSmall ? 120 : 32;
            int height = 32;

            if (jeed$mouseX >= x && jeed$mouseX <= x + width && jeed$mouseY >= y && jeed$mouseY <= y + height) {
                NativeCompat.setInventoryEffect(hoveredEffect, isSmall);
            }
        }
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE",
            target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            cancellable = true)
    private void jeed$cancelTooltips(GuiGraphics matrices, int mouseX, int mouseY, CallbackInfo info) {
        if (Jeed.suppressVanillaTooltips()) info.cancel();
    }



}