package net.mehvahdjukaar.jeed.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.InventoryScreenHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class DisplayEffectScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    protected DisplayEffectScreenMixin(T t, Inventory inventory, Component component) {
        super(t, inventory, component);
    }

    @Override
    public boolean mouseClicked(double x, double y, int activeButton) {

        if (Jeed.MOD_COMPAT.handleEffectMouseClicked(this, x, y, activeButton)) return true;

        return super.mouseClicked(x, y, activeButton);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int x, int y) {
        if (this.hoveredSlot == null && this.menu.getCarried().isEmpty()) {
            Jeed.MOD_COMPAT.handleEffectRenderTooltip(this, matrixStack, x, y);
        }
        super.renderTooltip(matrixStack, x, y);

    }
}
