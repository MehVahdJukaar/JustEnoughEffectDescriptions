package net.mehvahdjukaar.jeed.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.InventoryScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(DisplayEffectsScreen.class)
public abstract class DisplayEffectScreenMixin<T extends Container> extends ContainerScreen<T> {

    @Shadow
    protected boolean doRenderEffects;

    public DisplayEffectScreenMixin(T p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }

    @Override
    public boolean mouseClicked(double x, double y, int activeButton) {
        if (this.doRenderEffects) {
            EffectInstance effect = InventoryScreenHandler.getHoveredEffect(this, x, y, true);
            if (effect != null) {
                InventoryScreenHandler.onClickedEffect(effect, x, y, activeButton);
                return true;
            }
        }
        return super.mouseClicked(x, y, activeButton);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int x, int y) {
        if (this.hoveredSlot == null && this.minecraft.player.inventory.getCarried().isEmpty()) {
            if (this.doRenderEffects) {
                EffectInstance effect = InventoryScreenHandler.getHoveredEffect(this, x, y, true);
                if (effect != null) {
                    ITooltipFlag flag = Minecraft.getInstance().options.advancedItemTooltips ?
                            ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
                    List<ITextComponent> tooltip = EffectInstanceRenderer.INSTANCE.getTooltip(effect, flag);
                    this.renderComponentTooltip(matrixStack, tooltip, x, y);
                }
            }
        }
        super.renderTooltip(matrixStack, x, y);

    }

}