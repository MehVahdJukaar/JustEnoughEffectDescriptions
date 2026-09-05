package net.mehvahdjukaar.jeed.mixins.neoforge;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

//neoforge stubs out vanilla extractText and calls its own renderText overload instead
@Mixin(EffectsInInventory.class)
public abstract class EffectTooltipMixin {

    @WrapWithCondition(method = "renderText", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private boolean jeed$cancelTooltips(GuiGraphicsExtractor instance, Font font, List<Component> list,
                                        Optional<TooltipComponent> image, int mouseX, int mouseY) {
        return !Jeed.suppressVanillaTooltips();
    }
}
