package net.mehvahdjukaar.jeed.common;

import net.minecraft.world.effect.MobEffectInstance;

public interface IPlugin {

    void onClickedEffect(MobEffectInstance effect, double x, double y, int button);
}