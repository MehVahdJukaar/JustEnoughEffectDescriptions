package net.mehvahdjukaar.jeed.compat.fabric;

import fuzs.stylisheffects.api.client.MobEffectWidgetContext;
import fuzs.stylisheffects.api.client.StylishEffectsClientApi;
import fuzs.stylisheffects.api.client.event.MobEffectWidgetEvents;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.api.JeedAPI;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StylishEffectsCompat<T extends EffectRenderingInventoryScreen<?>> implements IEffectScreenExtension<T> {

    public static void init() {

        MobEffectWidgetEvents.CLICKED.register((evt, screen, x, y, button) -> {
            Jeed.PLUGIN.onClickedEffect(evt.effectInstance(), x, y, button);
            return true;
        });
        MobEffectWidgetEvents.TOOLTIP.register((evt, lines, flag) -> {
            if (!flag.isAdvanced()) {
                lines.remove(lines.size() - 1);
            }
            List<Component> newTooltip = EffectRenderer.getTooltipsWithDescription(evt.effectInstance(), flag, false);
            newTooltip.remove(0);
            lines.addAll(newTooltip);
        });

        JeedAPI.registerScreenExtension(EffectRenderingInventoryScreen.class, new StylishEffectsCompat<>());
        JeedAPI.registerScreenExtension(InventoryScreen.class, new StylishEffectsCompat<>());
        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, new StylishEffectsCompat<>());
    }

    @Nullable
    @Override
    public MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, CallReason reason) {
        if (reason != CallReason.RECIPE_KEY) return null;
        return StylishEffectsClientApi.getEffectScreenHandler().getInventoryHoveredEffect(screen, mouseX, mouseY)
                .map(MobEffectWidgetContext::effectInstance)
                .orElse(null);
    }
}
