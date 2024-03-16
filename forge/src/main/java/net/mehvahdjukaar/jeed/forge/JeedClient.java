package net.mehvahdjukaar.jeed.forge;

import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.mehvahdjukaar.jeed.compat.forge.StylishEffectsCompat;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

public class JeedClient {


    public static void init() {
        MinecraftForge.EVENT_BUS.register(JeedClient.class);

        NativeCompat.init();

        //credits to Fuzss for all the Stylish Effects mod compat
        if (ModList.get().isLoaded("stylisheffects")) {
            StylishEffectsCompat.init();
        }
    }

    private static IEffectScreenExtension<?> currentExt = null;
    private static Screen currentScreen = null;

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        currentExt = null;
        currentScreen = null;
    }
    
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        storeExtension(event.getScreen());
    }

    private static void storeExtension(Screen screen) {
        currentExt = ScreenExtensionsHandler.getExtension(screen);
        if (currentExt != null) {
            currentScreen = screen;
        } else currentScreen = null;
    }


    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render event) {
        if (currentExt != null) {
            Screen screen = event.getScreen();
            if (screen != currentScreen) {
                storeExtension(screen);
            }
            if (screen == currentScreen) {

                var effect = ((IEffectScreenExtension<Screen>) currentExt)
                        .getEffectAtPosition(screen, event.getMouseX(), event.getMouseY(), IEffectScreenExtension.CallReason.TOOLTIP);
                if (effect != null) {
                    ScreenExtensionsHandler.renderEffectTooltip(effect, screen, event.getGuiGraphics(),
                            event.getMouseX(), event.getMouseY(), currentExt.showDurationOnTooltip());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScreenMouseButton(ScreenEvent.MouseButtonPressed event) {
        if (currentExt != null) {
            Screen screen = event.getScreen();
            if (screen != currentScreen) {
                storeExtension(screen);
            }
            if (screen == currentScreen) {
                // we don't need extra checks here as this is only possible BETWEEN screen opening and closing event, so this cannot ever fail.
                var effect = ((IEffectScreenExtension<Screen>) currentExt)
                        .getEffectAtPosition(screen, event.getMouseX(), event.getMouseY(), IEffectScreenExtension.CallReason.MOUSE_CLICKED);
                if (effect != null) {
                    Jeed.PLUGIN.onClickedEffect(effect, event.getMouseX(), event.getMouseY(), event.getButton());
                }
            }
        }
    }


}
