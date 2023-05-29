package net.mehvahdjukaar.jeed.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
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
    private int jeed$mouseX, jeed$mouseY;

    @Inject(at = @At("HEAD"), method = "renderEffects")
    private void captureMouse(PoseStack matrices, int mouseX, int mouseY, CallbackInfo info) {
        jeed$hoveredEffect = null;
        jeed$mouseX = mouseX;
        jeed$mouseY = mouseY;
        NativeCompat.setInventoryEffect(null, false);
    }

    @Inject(method = "renderBackgrounds",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureEffect(PoseStack poseStack, int renderX, int yOffset, Iterable<MobEffectInstance> effects, boolean bl, CallbackInfo ci, int i, Iterator<MobEffectInstance> it, MobEffectInstance inst) {
        jeed$hoveredEffect = inst;
    }

    @ModifyArg(method = "renderBackgrounds",
            require = 2,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V")
    )
    private PoseStack captureHoveredEffect(PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
        Matrix4f last = poseStack.last().pose();
        Vector4f vec = new Vector4f(x, y, 0, 1f);
        vec.transform(last);
        x = (int) vec.x();
        y = (int) vec.y();
        if (jeed$mouseX >= x && jeed$mouseX <= x + width && jeed$mouseY >= y && jeed$mouseY <= y + height) {
            NativeCompat.setInventoryEffect(jeed$hoveredEffect, width < 33);
        }
        return poseStack;
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE",
            target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            cancellable = true)
    private void cancelTooltips(PoseStack matrices, int mouseX, int mouseY, CallbackInfo info) {
        if (Jeed.suppressVanillaTooltips()) info.cancel();
    }
}