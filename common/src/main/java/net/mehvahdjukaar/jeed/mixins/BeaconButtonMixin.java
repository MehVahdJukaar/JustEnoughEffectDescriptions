package net.mehvahdjukaar.jeed.mixins;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;

@Mixin(BeaconScreen.BeaconPowerButton.class)
public abstract class BeaconButtonMixin extends AbstractButton {


    public BeaconButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(method = "setEffect", at=@At("RETURN"))
    public void cancelTooltip(MobEffect effect, CallbackInfo ci){
        setTooltip(null);
    }

}
