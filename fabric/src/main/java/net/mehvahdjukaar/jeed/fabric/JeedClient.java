package net.mehvahdjukaar.jeed.fabric;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.compat.NativeCompat;
import net.mehvahdjukaar.jeed.compat.fabric.StylishEffectsCompat;

public class JeedClient {

    public static void init() {

        NativeCompat.init();
        //credits to Fuzss for all the Stylish Effects mod compat
        if (FabricLoader.getInstance().isModLoaded("stylisheffects")) {
            StylishEffectsCompat.init();
        }



        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                    var ext = ScreenExtensionsHandler.getExtension(screen);
                    if (ext != null) {
                        ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                            var effect = ext.getEffectAtPosition(screen1, mouseX, mouseY, IEffectScreenExtension.CallReason.TOOLTIP);
                            if (effect != null) {
                                ScreenExtensionsHandler.renderEffectTooltip(effect, screen1, matrices, mouseX, mouseY, ext.showDurationOnTooltip());
                            }
                        });
                        ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                            var effect = ext.getEffectAtPosition(screen1, mouseX, mouseY, IEffectScreenExtension.CallReason.MOUSE_CLICKED);
                            if (effect != null) {
                                Jeed.PLUGIN.onClickedEffect(effect, mouseX, mouseY, button);
                            }
                        });
                    }
                }
        );

    }


}
