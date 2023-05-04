package net.mehvahdjukaar.jeed.compat.fabric;

import fuzs.stylisheffects.api.client.MobEffectWidgetContext;
import fuzs.stylisheffects.api.client.StylishEffectsClientApi;
import fuzs.stylisheffects.api.client.event.MobEffectWidgetEvents;
import net.mehvahdjukaar.jeed.compat.IModCompat;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.InventoryScreenHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class StylishEffectsCompat implements IModCompat {

    @Override
    public void registerHandlers() {
        MobEffectWidgetEvents.CLICKED.register((evt, screen, x, y, button) -> {
            InventoryScreenHandler.onClickedEffect(evt.effectInstance(), x, y, button);
            return true;
        });
        MobEffectWidgetEvents.TOOLTIP.register((evt, lines, flag) -> {
            lines.clear();
            List<Component> newTooltip = EffectInstanceRenderer.INSTANCE.getTooltipsWithDescription(evt.effectInstance(), flag, false);
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
