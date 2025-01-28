package net.mehvahdjukaar.jeed.api;

import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Register here extensions for your Screen classes where effects are rendered.
 * This internally just invokes fabric/forge render screen events to render tooltips when an effect is hovered and REI/JEI API to open the effect category when one is clicked
 * <p>
 * Just call this stuff in your mod initializer
 */
public class JeedAPI {

    /**
     * You can pass here a superclasses, and it will apply to all its subclasses
     **/
    public static <T extends AbstractContainerScreen<?>> void registerScreenExtension(Class<T> screenClass, IEffectScreenExtension<T> extension) {
        ScreenExtensionsHandler.registerScreenExtension(screenClass, extension);
    }

    /**
     * This just overrides the vanilla inventory screen extension with a no-op one.
     * Call in case your mod removes or alter the effects in that screen, and you don't want to register your own proper extension to replace it
     */
    @SuppressWarnings("all")
    public static void disableVanillaInventoryScreenExtension() {
        disableExtension(CreativeModeInventoryScreen.class);
        disableExtension(InventoryScreen.class);
        disableExtension(EffectRenderingInventoryScreen.class);
    }

    public static <T extends AbstractContainerScreen> void disableExtension(Class<T> screenClass) {
        ScreenExtensionsHandler.unRegisterExtension(screenClass);
    }

    // Delegate calls. Just here for clarity

    public static void invokeEffectClicked(MobEffectInstance effectInstance, double mouseX, double mouseY, int button) {
        Jeed.PLUGIN.onClickedEffect(effectInstance, mouseX, mouseY, button);
    }

    public static List<Component> getEffectTooltip(MobEffectInstance effectInstance, TooltipFlag tooltipFlag,
                                                   boolean reactsToShift, boolean showDuration) {
        return EffectRenderer.getTooltipsWithDescription(effectInstance, tooltipFlag, reactsToShift, showDuration);
    }

    public static boolean isEffectHidden(Holder<MobEffect> effectInstance) {
        return effectInstance.is(Jeed.HIDDEN);
}


}
