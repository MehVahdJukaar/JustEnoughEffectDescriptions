package net.mehvahdjukaar.jeed.mixins.neoforge;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(EffectsInInventory.class)
public abstract class EffectTooltipMixin {

    @WrapWithCondition(method = "renderText(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;IIIIIILnet/minecraft/world/effect/MobEffectInstance;)V",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private boolean jeed$cancelTooltips(GuiGraphics instance, Font font, List<Component> list,
                                        Optional<TooltipComponent> image, int mouseX, int mouseY) {
        return !Jeed.suppressVanillaTooltips();
    }
}
