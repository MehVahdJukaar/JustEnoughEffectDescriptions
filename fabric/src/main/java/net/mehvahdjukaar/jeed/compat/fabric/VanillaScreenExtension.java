package net.mehvahdjukaar.jeed.compat.fabric;

import net.mehvahdjukaar.jeed.compat.AbstractVanillaScreenExtension;
import net.mehvahdjukaar.jeed.compat.IInventoryScreenExtension;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.effect.MobEffectInstance;

public class VanillaScreenExtension extends AbstractVanillaScreenExtension implements IInventoryScreenExtension {

    @Override
    public MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        return null;
    }

}
