package net.mehvahdjukaar.jeed.common.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenExtensionsHandlerImpl {

    public static final Map<Class<? extends Screen>, IEffectScreenExtension<? extends Screen>> EXTENSIONS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Screen, E extends IEffectScreenExtension<T>> E getExtension(T screen) {
        var c = (Class<T>) screen.getClass();
        return (E) EXTENSIONS.get(c);
    }

    public static <T extends Screen> void registerScreenExtension(Class<T> screenClass, IEffectScreenExtension<T> extension) {
        EXTENSIONS.put(screenClass, extension);
    }

    public static void init() {

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                    var ext = getExtension(screen);
                    if (ext != null) {
                        ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                            var effect = ext.getEffectAtPosition(screen1, mouseX, mouseY, true);
                            renderEffectTooltip(effect, screen1, matrices, mouseX, mouseY);
                        });
                    }
                }
        );
    }


    public static void renderEffectTooltip(@Nullable MobEffectInstance effect, Screen screen, PoseStack matrixStack, int x, int y) {
        if (effect != null) {
            TooltipFlag flag = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
            List<Component> tooltip = EffectRenderer.getTooltipsWithDescription(effect, flag, false);
            if (!tooltip.isEmpty()) {
                screen.renderComponentTooltip(matrixStack, tooltip, x, y);
            }
        }
    }

}
