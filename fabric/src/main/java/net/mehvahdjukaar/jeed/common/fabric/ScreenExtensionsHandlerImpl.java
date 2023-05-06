package net.mehvahdjukaar.jeed.common.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ScreenExtensionsHandlerImpl {



    public static void init() {

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                    var ext = ScreenExtensionsHandler.getExtension(screen);
                    if (ext != null) {
                        ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                            var effect = ext.getEffectAtPosition(screen1, mouseX, mouseY, true);
                            ScreenExtensionsHandler.renderEffectTooltip(effect, screen1, matrices, mouseX, mouseY);
                        });
                    }
                }
        );
    }





}
