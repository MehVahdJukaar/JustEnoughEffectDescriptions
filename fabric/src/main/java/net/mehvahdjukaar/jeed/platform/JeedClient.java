package net.mehvahdjukaar.jeed.platform;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.compat.NativeCompat;

public class JeedClient {

    public static void init() {

        NativeCompat.init();

        //credits to Fuzss for all the Stylish Effects mod compat. No 26.1 build of it yet
        //if (FabricLoader.getInstance().isModLoaded("stylisheffects")) {
        //    StylishEffectsCompat.init();
        //}

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                    var ext = ScreenExtensionsHandler.getExtension(screen);
                    if (ext != null) {
                        ScreenEvents.afterExtract(screen).register((screen1, graphics, mouseX, mouseY, tickDelta) -> {
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
