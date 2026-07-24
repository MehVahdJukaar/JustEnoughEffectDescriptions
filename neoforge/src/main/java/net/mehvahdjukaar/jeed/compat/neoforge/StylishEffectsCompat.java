// Stylish Effects 21.11.3 removed its whole fuzs.stylisheffects.api.v1.client package, so none of the hooks
// below exist anymore. Kept here, commented out, in case the API comes back. Still written against the 1.21 APIs.
//package net.mehvahdjukaar.jeed.compat.neoforge;
//
//import com.mojang.blaze3d.platform.InputConstants;
//import fuzs.stylisheffects.api.v1.client.EffectScreenHandler;
//import fuzs.stylisheffects.api.v1.client.MobEffectWidgetContext;
//import fuzs.stylisheffects.neoforge.api.v1.client.NeoForgeMobEffectWidgetEvent;
//import net.mehvahdjukaar.jeed.Jeed;
//import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
//import net.mehvahdjukaar.jeed.api.JeedAPI;
//import net.mehvahdjukaar.jeed.common.EffectRenderer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
//import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
//import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.neoforged.neoforge.common.NeoForge;
//
//import java.util.List;
//
//public class StylishEffectsCompat<T extends EffectRenderingInventoryScreen<?>> implements IEffectScreenExtension<T> {
//
//    public static void init() {
//        NeoForge.EVENT_BUS.addListener((final NeoForgeMobEffectWidgetEvent.MouseClicked evt) -> {
//            Jeed.PLUGIN.onClickedEffect(evt.getContext().effectInstance(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
//            evt.setCanceled(true);
//        });
//        NeoForge.EVENT_BUS.addListener((final NeoForgeMobEffectWidgetEvent.EffectTooltip evt) -> {
//            List<Component> lines = evt.getTooltipLines();
//            if(!evt.getTooltipFlag().isAdvanced() && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue())){
//                lines.remove(lines.size()-1);
//            }
//            List<Component> newTooltip = EffectRenderer.getTooltipsWithDescription(evt.getContext().effectInstance(),
//                    evt.getTooltipFlag(), false, false);
//            newTooltip.remove(0);
//            lines.addAll(newTooltip);
//        });
//
//        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, new StylishEffectsCompat<>());
//        JeedAPI.registerScreenExtension(InventoryScreen.class, new StylishEffectsCompat<>());
//        JeedAPI.registerScreenExtension(EffectRenderingInventoryScreen.class, new StylishEffectsCompat<>());
//    }
//
//    @Override
//    public MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, CallReason reason) {
//        if (reason != CallReason.RECIPE_KEY) return null; //rest is handled by Stylish Effect events above
//        return EffectScreenHandler.INSTANCE.getInventoryHoveredEffect(screen, mouseX, mouseY)
//                .map(MobEffectWidgetContext::effectInstance)
//                .orElse(null);
//    }
//}
