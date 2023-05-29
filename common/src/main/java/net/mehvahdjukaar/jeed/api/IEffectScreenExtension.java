package net.mehvahdjukaar.jeed.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;


@FunctionalInterface
public interface IEffectScreenExtension<T extends Screen> {

    IEffectScreenExtension NO_OP = (screen, mouseX, mouseY, isForTooltip) -> null;

    @Nullable
    MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, CallReason callReason);

    default boolean showDurationOnTooltip(){
        return false;
    };

    enum CallReason {
        TOOLTIP, RECIPE_KEY, MOUSE_CLICKED;

        public boolean isForRender() {
            return this == TOOLTIP;
        }
    }

}
