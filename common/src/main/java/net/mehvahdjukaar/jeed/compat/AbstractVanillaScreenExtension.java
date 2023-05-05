package net.mehvahdjukaar.jeed.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class AbstractVanillaScreenExtension implements IInventoryScreenExtension {

    @Override
    public void handleEffectRenderTooltip(AbstractContainerScreen<?> screen, PoseStack matrixStack, int x, int y) {
        MobEffectInstance effect = getHoveredEffect(screen, x, y, true);

        TooltipFlag flag = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        List<Component> tooltip = EffectRenderer.getTooltipsWithDescription(effect, flag, false);
        if (!tooltip.isEmpty()) {
            screen.renderComponentTooltip(matrixStack, tooltip, x, y);
        }
    }

    @Override
    public boolean handleEffectMouseClicked(AbstractContainerScreen<?> screen, double x, double y, int activeButton) {
        MobEffectInstance effect = getHoveredEffect(screen, x, y, false);
        if (effect != null) {
            Jeed.PLUGIN.onClickedEffect(effect, x, y, activeButton);
            return true;
        }
        return false;
    }

}
