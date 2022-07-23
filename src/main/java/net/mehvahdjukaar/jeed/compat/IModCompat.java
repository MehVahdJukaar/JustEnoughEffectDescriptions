package net.mehvahdjukaar.jeed.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffectInstance;

public interface IModCompat {

    default void registerHandlers() {

    }

    MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall);

    default boolean handleEffectMouseClicked(AbstractContainerScreen<?> screen, double x, double y, int activeButton) {
        return false;
    }

    default void handleEffectRenderTooltip(AbstractContainerScreen<?> screen, PoseStack matrixStack, int x, int y) {

    }
}
