package net.mehvahdjukaar.jeed.compat.fabric;

import fuzs.stylisheffects.api.client.MobEffectWidgetContext;
import fuzs.stylisheffects.api.client.StylishEffectsClientApi;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.api.JeedAPI;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StylishEffectsCompat<T extends EffectRenderingInventoryScreen<?>> implements IEffectScreenExtension<T> {

    public static void init() {
        /*
        MobEffectWidgetEvents.CLICKED.register((evt, screen, x, y, button) -> {
            Jeed.PLUGIN.onClickedEffect(evt.effectInstance(), x, y, button);
            return true;
        });
        MobEffectWidgetEvents.TOOLTIP.register((evt, lines, flag) -> {
            lines.clear();
            List<Component> newTooltip = EffectInstanceRenderer.getTooltipsWithDescription(evt.effectInstance(), flag, false);
            lines.addAll(newTooltip);
        });*/

        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, new StylishEffectsCompat<>());
        JeedAPI.registerScreenExtension(InventoryScreen.class, new StylishEffectsCompat<>());
    }

    @Nullable
    @Override
    public MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        return StylishEffectsClientApi.getEffectScreenHandler().getInventoryHoveredEffect(screen, mouseX, mouseY)
                .map(context -> context.renderer().isCompact() && ignoreIfSmall ? null : context)
                .map(MobEffectWidgetContext::effectInstance)
                .orElse(null);
    }
}
