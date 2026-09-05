package net.mehvahdjukaar.jeed.platform;

import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.mehvahdjukaar.jeed.data.JEEDProviderManager;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public class JeedClient {


    public static void init(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(JeedClient.class);
        modBus.addListener((AddClientReloadListenersEvent event) ->
                event.addListener(Jeed.res("providers"), JEEDProviderManager.INSTANCE));

        NativeCompat.init();

        //credits to Fuzss for all the Stylish Effects mod compat. Its api package is gone as of 21.11.3
        //if (ModList.get().isLoaded("stylisheffects")) {
        //    StylishEffectsCompat.init();
        //}
    }

    private static IEffectScreenExtension<?> currentExt = null;
    private static Screen currentScreen = null;

    @SubscribeEvent
    public static void onTagSync(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            JEEDProviderManager.applyWithLevel(event.getLookupProvider());
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        JEEDProviderManager.resetWithLevel();
    }

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
    public static void onScreenRender(ScreenEvent.Render.Pre event) {
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
    public static void onScreenMouseButton(ScreenEvent.MouseButtonPressed.Pre event) {
        if (Jeed.EMI) return;
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
                    event.setCanceled(true);
                }
            }
        }
    }


}
