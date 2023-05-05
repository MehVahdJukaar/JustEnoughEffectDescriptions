package net.mehvahdjukaar.jeed.compat.fabric;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.jeed.compat.AbstractVanillaScreenExtension;
import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VanillaScreenExtension extends AbstractVanillaScreenExtension implements IInventoryScreenExtension {

    @Override
    public MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
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
            if (!full && ignoreIfSmall) return null;
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
        return null;
    }

}
