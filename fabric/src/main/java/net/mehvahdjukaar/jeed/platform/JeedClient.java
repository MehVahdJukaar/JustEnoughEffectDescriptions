package net.mehvahdjukaar.jeed.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.mehvahdjukaar.jeed.data.JEEDProviderManager;
import net.minecraft.server.packs.PackType;

public class JeedClient {

    public static void init() {

        NativeCompat.init();

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(Jeed.res("providers"), JEEDProviderManager.INSTANCE);
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) JEEDProviderManager.applyWithLevel(registries);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> JEEDProviderManager.resetWithLevel());

        //credits to Fuzss for all the Stylish Effects mod compat. Its api package is gone as of 21.11.3
        //if (FabricLoader.getInstance().isModLoaded("stylisheffects")) {
        //    StylishEffectsCompat.init();
        //}

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                    var ext = ScreenExtensionsHandler.getExtension(screen);
                    if (ext != null) {
                        //before, not after: the screen flushes deferred tooltips at the end of its own render
                        ScreenEvents.beforeRender(screen).register((screen1, graphics, mouseX, mouseY, tickDelta) -> {
                            var effect = ext.getEffectAtPosition(screen1, mouseX, mouseY, IEffectScreenExtension.CallReason.TOOLTIP);
                            if (effect != null) {
                                ScreenExtensionsHandler.renderEffectTooltip(effect, screen1, graphics,
                                        mouseX, mouseY, ext.showDurationOnTooltip());
                            }
                        });
                        if (Jeed.EMI) return;

                        ScreenMouseEvents.afterMouseClick(screen).register((screen1, event, doubleClick) -> {
                            var effect = ext.getEffectAtPosition(screen1, event.x(), event.y(), IEffectScreenExtension.CallReason.MOUSE_CLICKED);
                            if (effect != null) {
                                Jeed.PLUGIN.onClickedEffect(effect, event.x(), event.y(), event.button());
                            }
                            return false;
                        });
                    }
                }
        );
    }
}
