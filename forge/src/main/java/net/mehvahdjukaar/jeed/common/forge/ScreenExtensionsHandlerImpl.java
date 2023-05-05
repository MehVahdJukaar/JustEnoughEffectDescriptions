package net.mehvahdjukaar.jeed.common.forge;

import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScreenExtensionsHandlerImpl {


    public static void init() {
        MinecraftForge.EVENT_BUS.register(ScreenExtensionsHandlerImpl.class);
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init event){
        if(event.getScreen() instanceof EffectRenderingInventoryScreen){
            MinecraftForge.EVENT_BUS.register(TOOLTIP_RENDERER);
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event){
        if(event.getScreen() instanceof EffectRenderingInventoryScreen){
            MinecraftForge.EVENT_BUS.unregister(TOOLTIP_RENDERER);
        }
    }

    public static final Object TOOLTIP_RENDERER = new Object(){
        @SubscribeEvent
        public static void renderEffectTooltips(ScreenEvent.Render event){
            int aa = 1;
        }
    };


}
