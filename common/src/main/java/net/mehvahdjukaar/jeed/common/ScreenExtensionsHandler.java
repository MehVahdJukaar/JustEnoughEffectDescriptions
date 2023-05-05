package net.mehvahdjukaar.jeed.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.minecraft.client.gui.screens.Screen;

import java.util.IdentityHashMap;
import java.util.Map;

public class ScreenExtensionsHandler {

    public static final Map<Class<? extends Screen>, IEffectScreenExtension<? extends Screen>> EXTENSIONS = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Screen, E extends IEffectScreenExtension<T>> E getExtension(T screen) {
        var c = (Class<T>) screen.getClass();
        var direct = EXTENSIONS.get(c);
        if (direct != null) return (E) direct;
        for (var e : EXTENSIONS.entrySet()) {
            if (e.getKey().isInstance(screen)) {
                return (E) e.getValue();
            }
        }
        return null;
    }

    public static <T extends Screen> void registerScreenExtension(Class<T> screenClass, IEffectScreenExtension<T> extension) {
        EXTENSIONS.put(screenClass, extension);
    }

    public static <T extends Screen> void unRegisterExtension(Class<T> screenClass) {
        EXTENSIONS.remove(screenClass);
    }


    @ExpectPlatform
    public static void init(){
        throw new AssertionError();
    }
}
