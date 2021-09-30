package net.mehvahdjukaar.jeed.mixins;

import net.minecraft.client.gui.DisplayEffectsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisplayEffectsScreen.class)
public interface DisplayEffectScreenAccessor{

    @Accessor("doRenderEffects")
    boolean hasEffects();

}