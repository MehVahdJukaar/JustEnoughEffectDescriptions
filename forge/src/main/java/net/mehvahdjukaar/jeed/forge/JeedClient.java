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

import java.util.function.Consumer;

public class JeedClient {


    public static void init() {
        MinecraftForge.EVENT_BUS.register(JeedClient.class);

        NativeCompat.init();

        //credits to Fuzss for all the Stylish Effects mod compat
        if (ModList.get().isLoaded("stylisheffects")) {
            StylishEffectsCompat.init();
        }
    }

    private static boolean screenChanged = false;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event) {
        screenChanged = true;
        Screen screen = event.getScreen();
        var ext = ScreenExtensionsHandler.getExtension(screen);
        if (ext != null) {
            MinecraftForge.EVENT_BUS.addListener(new Consumer<ScreenEvent.Render>() {
                @Override
                public void accept(ScreenEvent.Render event) {
                    if (screenChanged) {
                        MinecraftForge.EVENT_BUS.unregister(this);
                    } else {
                        var effect = ext.getEffectAtPosition(screen, event.getMouseX(), event.getMouseY(), IEffectScreenExtension.CallReason.TOOLTIP);
                        if (effect != null) {
                            ScreenExtensionsHandler.renderEffectTooltip(effect, screen, event.getPoseStack(), event.getMouseX(), event.getMouseY(),
                                    ext.showDurationOnTooltip());
                        }
                    }
                }
            });
            MinecraftForge.EVENT_BUS.addListener(new Consumer<ScreenEvent.MouseButtonPressed>() {
                @Override
                public void accept(ScreenEvent.MouseButtonPressed event) {
                    if (screenChanged) {
                        MinecraftForge.EVENT_BUS.unregister(this);
                    } else {
                        var effect = ext.getEffectAtPosition(screen, event.getMouseX(), event.getMouseY(), IEffectScreenExtension.CallReason.MOUSE_CLICKED);
                        if (effect != null) {
                            Jeed.PLUGIN.onClickedEffect(effect, event.getMouseX(), event.getMouseY(), event.getButton());
                        }
                    }
                }
            });

            screenChanged = false;
        }
    }


}
