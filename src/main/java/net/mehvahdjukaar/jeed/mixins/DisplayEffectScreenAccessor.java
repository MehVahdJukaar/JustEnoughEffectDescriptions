package net.mehvahdjukaar.jeed.mixins;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EffectRenderingInventoryScreen.class)
public interface DisplayEffectScreenAccessor{

    //@Accessor("doRenderEffects")
    //boolean hasEffects();

}