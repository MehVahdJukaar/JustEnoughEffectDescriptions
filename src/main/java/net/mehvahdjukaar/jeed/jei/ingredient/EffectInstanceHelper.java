package net.mehvahdjukaar.jeed.jei.ingredient;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;

public class EffectInstanceHelper implements IIngredientHelper<EffectInstance> {
    public EffectInstanceHelper() {
    }

    @Nullable
    public EffectInstance getMatch(Iterable<EffectInstance> ingredients, EffectInstance toMatch) {
        for (EffectInstance effect : ingredients) {
            if (toMatch.equals(effect)) {
                return effect;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(EffectInstance ingredient) {
        ITextComponent displayName = ingredient.getEffect().getDisplayName();
        return displayName.getString();
    }

    @Override
    public String getUniqueId(EffectInstance ingredient) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        return "effect:" + registryName;
    }

    @Override
    public String getModId(EffectInstance ingredient) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        if (registryName == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("effect.getRegistryName() returned null for: " + ingredientInfo);
        } else {
            return registryName.getNamespace();
        }
    }

    @Override
    public String getResourceId(EffectInstance ingredient) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        if (registryName == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("ingredient.getRegistryName() returned null for: " + ingredientInfo);
        } else {
            return registryName.getPath();
        }
    }

    @Override
    public Iterable<Integer> getColors(EffectInstance ingredient) {
        return Collections.singletonList(ingredient.getEffect().getColor());
    }
    @Override
    public ItemStack getCheatItemStack(EffectInstance ingredient) {
        return PotionUtils.setCustomEffects(new ItemStack(Items.POTION),
                Collections.singletonList(ingredient));
    }

    @Override
    public EffectInstance copyIngredient(EffectInstance ingredient) {
        return new EffectInstance(ingredient.getEffect(), ingredient.getDuration(), ingredient.getAmplifier(),
                ingredient.isAmbient(), ingredient.isVisible(), ingredient.showIcon());
    }

    @Override
    public EffectInstance normalizeIngredient(EffectInstance ingredient) {
        return new EffectInstance(ingredient.getEffect(), 200);
    }

    @Override
    public String getErrorInfo(@Nullable EffectInstance ingredient) {
        if (ingredient == null) {
            return "null";
        } else {
            ToStringHelper toStringHelper = MoreObjects.toStringHelper(FluidStack.class);
            Effect effect = ingredient.getEffect();
            if (effect != null) {
                ITextComponent displayName = ingredient.getEffect().getDisplayName();
                toStringHelper.add("Effect", displayName.getString());
            } else {
                toStringHelper.add("Effect", "null");
            }

            toStringHelper.add("Duration", ingredient.getDuration());
            toStringHelper.add("Amplifier", ingredient.getAmplifier());

            return toStringHelper.toString();
        }
    }
}
