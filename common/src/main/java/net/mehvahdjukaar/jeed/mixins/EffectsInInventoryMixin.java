package net.mehvahdjukaar.jeed.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EffectsInInventory.class)
public abstract class EffectsInInventoryMixin {

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
                    target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;IIZI)I")
    )
    private int jeed$captureHoveredEffect(EffectsInInventory instance, GuiGraphicsExtractor graphics, Font font,
                                          Component effectName, Component duration, int x, int y, boolean isAmbient,
                                          int maxTextureWidth, Operation<Integer> original,
                                          @Local MobEffectInstance hoveredEffect) {
        int width = original.call(instance, graphics, font, effectName, duration, x, y, isAmbient, maxTextureWidth);
        int height = 32;

        if (jeed$mouseX >= x && jeed$mouseX <= x + width && jeed$mouseY >= y && jeed$mouseY <= y + height) {
            boolean compact = maxTextureWidth <= 32;
            NativeCompat.setInventoryEffect(hoveredEffect, compact);
        }
        return width;
    }
}
