package net.mehvahdjukaar.jeed.compat.fabric;

import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.api.JeedAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.List;

public class NativeCompat<T extends EffectRenderingInventoryScreen<?>> implements IEffectScreenExtension<T> {

    public static void init() {
        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, new NativeCompat<>());
        JeedAPI.registerScreenExtension(InventoryScreen.class, new NativeCompat<>());
        JeedAPI.registerScreenExtension(EffectRenderingInventoryScreen.class, new NativeCompat<>());
    }

    @Override
    public MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, boolean isTooltip) {
        if (!isTooltip || (screen.hoveredSlot == null && screen.getMenu().getCarried().isEmpty())) {

            int minX;
            boolean cancelShift = false;
            if (cancelShift)
                minX = (screen.width - screen.imageWidth) / 2;
            else
                minX = screen.leftPos + screen.imageWidth + 2;
            int x = screen.width - minX;
            Collection<MobEffectInstance> collection = Minecraft.getInstance().player.getActiveEffects();
            if (!collection.isEmpty() && x >= 32) {

                boolean full = x >= 120;
                if (!full && isTooltip) return null;
                int width = full ? 120 : 32;
                if (mouseX > minX && mouseX < minX + width) {

                    int spacing = 33;
                    if (collection.size() > 5) {
                        spacing = 132 / (collection.size() - 1);
                    }


                    List<MobEffectInstance> iterable = collection.stream().sorted().toList();

                    int minY = screen.topPos;
                    int maxHeight = iterable.size() * spacing;

                    if (mouseY > minY && mouseY < minY + maxHeight) {
                        return iterable.get((int) ((mouseY - minY) / spacing));
                    }
                }
            }
        }
        return null;
    }

}
