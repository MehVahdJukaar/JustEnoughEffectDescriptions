package net.mehvahdjukaar.jeed.plugin.jei.ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EffectInstanceRenderer extends EffectRenderer implements IIngredientRenderer<MobEffectInstance> {

    public static final EffectInstanceRenderer INSTANCE = new EffectInstanceRenderer(true);
    public static final EffectInstanceRenderer INSTANCE_SLOT = new EffectInstanceRenderer(false);

    public EffectInstanceRenderer(boolean offset) {
        super(offset);
    }

    @Override
    public List<Component> getTooltip(MobEffectInstance effectInstance, TooltipFlag tooltipFlag) {
        return getTooltipsWithDescription(effectInstance, tooltipFlag, false, false);
    }

    @Override
    public int getWidth() {
        return offset ? 16 : 18;
    }

    @Override
    public int getHeight() {
        return offset ? 16 : 18;
    }
}
