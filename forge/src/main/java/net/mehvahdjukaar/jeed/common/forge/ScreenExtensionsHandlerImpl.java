package net.mehvahdjukaar.jeed.common.forge;

import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Consumer;

public class ScreenExtensionsHandlerImpl {


    public static void init() {
        MinecraftForge.EVENT_BUS.register(ScreenExtensionsHandlerImpl.class);
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        isEvScreen = false;
        var ext = ScreenExtensionsHandler.getExtension(event.getScreen());
        if (ext != null) {
            MinecraftForge.EVENT_BUS.addListener(new Ev<>(ext, event.getScreen()));
            isEvScreen = true;
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.MouseButtonPressed event) {
        if (isEvScreen) {
            var ext = ScreenExtensionsHandler.getExtension(event.getScreen());
            if (ext != null) {
                var eff = ext.getEffectAtPosition(event.getScreen(), event.getMouseX(), event.getMouseY(), false);
                if (eff != null) {
                    Jeed.PLUGIN.onClickedEffect(eff, event.getMouseX(), event.getMouseY(), event.getButton());
                }
            }
        }
    }

    private static boolean isEvScreen = false;

    private static class Ev<T extends Screen> implements Consumer<ScreenEvent.Render> {
        private final IEffectScreenExtension<T> ex;
        private final T screen;

        private Ev(IEffectScreenExtension<T> ex, T screen) {
            this.ex = ex;
            this.screen = screen;
        }

        @Override
        public void accept(ScreenEvent.Render event) {
            if (!isEvScreen) {
                MinecraftForge.EVENT_BUS.unregister(this);
            } else {
                var effect = ex.getEffectAtPosition(screen, event.getMouseX(), event.getMouseY(), true);
                ScreenExtensionsHandler.renderEffectTooltip(effect, screen, event.getPoseStack(), event.getMouseX(), event.getMouseY());
            }
        }
    }


}
