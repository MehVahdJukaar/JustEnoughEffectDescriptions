package net.mehvahdjukaar.jeed.api;

import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

/**
 * Register here extensions for your Screen classes where effects are rendered.
 * This internally just invokes fabric/forge render screen events to render tooltips when an effect is hovered and REI/JEI API to open the effect category when one is clicked
 */
public class JeedAPI {

    /**
     * You can pass here a superclasses, and it will apply to all its subclasses
     **/
    public static <T extends Screen> void registerScreenExtension(Class<T> screenClass, IEffectScreenExtension<T> extension) {
        ScreenExtensionsHandler.registerScreenExtension(screenClass,extension);
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

    public static <T extends Screen> void disableExtension(Class<T> screenClass) {
        ScreenExtensionsHandler.unRegisterExtension(screenClass);
    }

}
