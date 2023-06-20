package net.mehvahdjukaar.jeed.mixins;


import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.joml.Matrix4f;
import org.joml.Vector4i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;


@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin {

    @Unique
    private MobEffectInstance jeed$hoveredEffect;
    @Unique
    private GuiGraphics jeed$guiGraphics;

    @Unique
    private int jeed$mouseX, jeed$mouseY;

    @Inject(at = @At("HEAD"), method = "renderEffects")
    private void captureMouse(GuiGraphics matrices, int mouseX, int mouseY, CallbackInfo info) {
        jeed$hoveredEffect = null;
        jeed$mouseX = mouseX;
        jeed$mouseY = mouseY;
        NativeCompat.setInventoryEffect(null, false);
    }

    @Inject(method = "renderBackgrounds",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
                    shift = At.Shift.BY,
                    by = 3),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureEffect(GuiGraphics graphics, int renderX, int yOffset, Iterable<MobEffectInstance> effects, boolean bl, CallbackInfo ci, int i,
                               Iterator var7, MobEffectInstance inst) {
        jeed$hoveredEffect = inst;
        jeed$guiGraphics = graphics;
    }

    @ModifyArg(method = "renderBackgrounds",
            require = 2,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V")
    )

    private ResourceLocation captureHoveredEffect(ResourceLocation res, int x, int y, int u, int v, int width, int height) {
        if (jeed$hoveredEffect != null) {
            Matrix4f last = jeed$guiGraphics.pose().last().pose();
            Vector4i vec = new Vector4i(x, y, 0, 1);
            last.mul(last);
            x = vec.x();
            y = vec.y();

            if (jeed$mouseX >= x && jeed$mouseX <= x + width && jeed$mouseY >= y && jeed$mouseY <= y + height) {
                NativeCompat.setInventoryEffect(jeed$hoveredEffect, width < 33);
            }
        }
        return res;
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE",
            target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            cancellable = true)
    private void cancelTooltips(GuiGraphics matrices, int mouseX, int mouseY, CallbackInfo info) {
        if (Jeed.suppressVanillaTooltips()) info.cancel();
    }

}