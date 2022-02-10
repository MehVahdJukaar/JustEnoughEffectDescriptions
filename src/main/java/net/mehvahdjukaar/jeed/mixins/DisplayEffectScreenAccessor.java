package net.mehvahdjukaar.jeed.mixins;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EffectRenderingInventoryScreen.class)
public interface DisplayEffectScreenAccessor{

    @Accessor("cancelShift")
    boolean isCancelShift();

}