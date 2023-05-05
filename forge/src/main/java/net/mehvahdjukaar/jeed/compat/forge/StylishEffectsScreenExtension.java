package net.mehvahdjukaar.jeed.compat.forge;

import fuzs.stylisheffects.api.client.MobEffectWidgetContext;
import fuzs.stylisheffects.api.client.StylishEffectsClientApi;
import fuzs.stylisheffects.api.client.event.MobEffectWidgetEvent;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class StylishEffectsScreenExtension implements IInventoryScreenExtension {

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final MobEffectWidgetEvent.MouseClicked evt) -> {
            Jeed.PLUGIN.onClickedEffect(evt.getContext().effectInstance(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            evt.setCanceled(true);
        });
        MinecraftForge.EVENT_BUS.addListener((final MobEffectWidgetEvent.EffectTooltip evt) -> {
            List<Component> lines = evt.getTooltipLines();
            lines.clear();
            List<Component> newTooltip = EffectInstanceRenderer.getTooltipsWithDescription(evt.getContext().effectInstance(), evt.getTooltipFlag(), false);
            lines.addAll(newTooltip);
        });
    }

    @Override
    public MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        return StylishEffectsClientApi.getEffectScreenHandler().getInventoryHoveredEffect(screen, mouseX, mouseY)
                .map(context -> context.renderer().isCompact() && ignoreIfSmall ? null : context)
                .map(MobEffectWidgetContext::effectInstance)
                .orElse(null);
    }
}
