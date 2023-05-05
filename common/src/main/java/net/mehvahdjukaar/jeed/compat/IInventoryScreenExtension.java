package net.mehvahdjukaar.jeed.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffectInstance;

//client class
public interface IInventoryScreenExtension {

    IInventoryScreenExtension INSTANCE = IInventoryScreenExtension.createInstance();

    default void registerHandlers() {

    }

    MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall);

    default boolean handleEffectMouseClicked(AbstractContainerScreen<?> screen, double x, double y, int activeButton) {
        return false;
    }

    default void handleEffectRenderTooltip(AbstractContainerScreen<?> screen, PoseStack matrixStack, int x, int y) {

    }


    @ExpectPlatform
    static IInventoryScreenExtension createInstance() {
        throw new AssertionError();
    }
}
