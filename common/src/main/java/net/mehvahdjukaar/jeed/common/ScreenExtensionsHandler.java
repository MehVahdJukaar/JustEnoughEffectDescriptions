package net.mehvahdjukaar.jeed.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.minecraft.client.gui.screens.Screen;

import java.util.Map;

public class ScreenExtensionsHandler {

    @ExpectPlatform
    public static Map<? extends Screen, IEffectScreenExtension<? extends Screen>> getAllExtensions(){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Screen, E extends IEffectScreenExtension<T>> E getExtension(T screen) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Screen> void registerScreenExtension(Class<T> screenClass, IEffectScreenExtension<T> extension) {
        throw new AssertionError();
    }

}
