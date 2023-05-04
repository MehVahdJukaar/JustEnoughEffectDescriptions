package net.mehvahdjukaar.jeed.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.InventoryScreenHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class NativeModCompat implements IModCompat {

    @Override
    public MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        return InventoryScreenHandler.getHoveredEffect(screen, mouseX, mouseY, ignoreIfSmall);
    }

    @Override
    public void handleEffectRenderTooltip(AbstractContainerScreen<?> screen, PoseStack matrixStack, int x, int y) {
        MobEffectInstance effect = InventoryScreenHandler.getHoveredEffect(screen, x, y, true);

        TooltipFlag flag = screen.getMinecraft().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        List<Component> tooltip = EffectInstanceRenderer.INSTANCE.getTooltipsWithDescription(effect, flag, false);
        if (!tooltip.isEmpty()) {
            screen.renderComponentTooltip(matrixStack, tooltip, x, y);
        }
    }

    @Override
    public boolean handleEffectMouseClicked(AbstractContainerScreen<?> screen, double x, double y, int activeButton) {
        MobEffectInstance effect = InventoryScreenHandler.getHoveredEffect(screen, x, y, false);
        if (effect != null) {
            InventoryScreenHandler.onClickedEffect(effect, x, y, activeButton);
            return true;
        }
        return false;
    }
}
