package net.mehvahdjukaar.jeed.compat.forge;

import net.mehvahdjukaar.jeed.compat.AbstractVanillaScreenExtension;
import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class VanillaScreenExtension extends AbstractVanillaScreenExtension implements IInventoryScreenExtension {


    @Override
    public MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        int minX;
        boolean cancelShift = false;
        if (cancelShift)
            minX = (screen.width - screen.getXSize()) / 2;
        else
            minX = screen.getGuiLeft() + screen.getXSize() + 2;
        int x = screen.width - minX;
        Collection<MobEffectInstance> collection = Minecraft.getInstance().player.getActiveEffects();
        if (!collection.isEmpty() && x >= 32) {

            boolean full = x >= 120;
            var event = ForgeHooksClient.onScreenPotionSize(screen, x, !full, minX);
            if (event.isCanceled()) return null;
            full = !event.isCompact();
            minX = event.getHorizontalOffset();
            if (!full && ignoreIfSmall) return null;
            int width = full ? 120 : 32;
            if (mouseX > minX && mouseX < minX + width) {

                int spacing = 33;
                if (collection.size() > 5) {
                    spacing = 132 / (collection.size() - 1);
                }


                List<MobEffectInstance> iterable = collection.stream().filter(ForgeHooksClient::shouldRenderEffect).sorted().collect(Collectors.toList());

                int minY = screen.getGuiTop();
                int maxHeight = iterable.size() * spacing;

                if (mouseY > minY && mouseY < minY + maxHeight) {
                    return iterable.get((int) ((mouseY - minY) / spacing));
                }
            }
        }
        return null;
    }

}
