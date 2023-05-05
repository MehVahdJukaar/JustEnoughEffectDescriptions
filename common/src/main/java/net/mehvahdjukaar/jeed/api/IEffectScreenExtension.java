package net.mehvahdjukaar.jeed.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;


@FunctionalInterface
public interface IEffectScreenExtension<T extends Screen> {

    IEffectScreenExtension NO_OP = (screen, mouseX, mouseY, isForTooltip) -> null;

    /**
     * @param isForTooltip true if this was called to render the tooltip. false if was called when clicked
     * @return effect at this position
     */
    @Nullable
    MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, boolean isForTooltip);

}
