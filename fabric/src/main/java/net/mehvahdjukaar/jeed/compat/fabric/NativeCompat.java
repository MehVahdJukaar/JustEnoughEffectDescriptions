package net.mehvahdjukaar.jeed.compat.fabric;

import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.api.JeedAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class NativeCompat {

    public static void init() {
        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, new InventoryScreenCompat<>());
        JeedAPI.registerScreenExtension(InventoryScreen.class, new InventoryScreenCompat<>());
        JeedAPI.registerScreenExtension(EffectRenderingInventoryScreen.class, new InventoryScreenCompat<>());
        JeedAPI.registerScreenExtension(BeaconScreen.class, new BeaconScreenCompat());
    }

    private static class BeaconScreenCompat implements IEffectScreenExtension<BeaconScreen> {

        @Nullable
        @Override
        public MobEffectInstance getEffectAtPosition(BeaconScreen screen, double mouseX, double mouseY, CallReason reason) {
            if (reason != CallReason.MOUSE_CLICKED) {
                for (var b : screen.beaconButtons) {
                    if (b instanceof BeaconScreen.BeaconPowerButton pb) {
                        if (pb.isShowingTooltip()) {
                            return new MobEffectInstance(pb.effect);
                        }
                    }
                }
            }
            return null;
        }
    }

    private static class InventoryScreenCompat<T extends EffectRenderingInventoryScreen<?>> implements IEffectScreenExtension<T> {

        @Override
        public MobEffectInstance getEffectAtPosition(T screen, double mouseX, double mouseY, CallReason reason) {
            if (!reason.isForRender() || (screen.hoveredSlot == null && screen.getMenu().getCarried().isEmpty())) {

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
                    if (!full && reason.isForRender()) return null;
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
}