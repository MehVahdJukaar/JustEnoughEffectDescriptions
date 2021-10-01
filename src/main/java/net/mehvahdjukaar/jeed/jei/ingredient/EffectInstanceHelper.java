package net.mehvahdjukaar.jeed.jei.ingredient;//


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.mehvahdjukaar.jeed.jei.JEIPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;

public class EffectInstanceHelper implements IIngredientHelper<MobEffectInstance> {
    public EffectInstanceHelper() {
    }

    @Nullable
    public MobEffectInstance getMatch(Iterable<MobEffectInstance> ingredients, MobEffectInstance toMatch) {
        for (MobEffectInstance effect : ingredients) {
            if (toMatch.equals(effect)) {
                return effect;
            }
        }
        return null;
    }

    @Override
    public IIngredientType<MobEffectInstance> getIngredientType() {
        return JEIPlugin.EFFECT;
    }

    @Override
    @Nullable
    public MobEffectInstance getMatch(Iterable<MobEffectInstance> iterable, MobEffectInstance mobEffectInstance, UidContext uidContext) {
        for (MobEffectInstance e : iterable){
            if(e.getEffect().equals(mobEffectInstance.getEffect()))return e;
        }
        return null;
    }

    @Override
    public String getDisplayName(MobEffectInstance ingredient) {
        Component displayName = ingredient.getEffect().getDisplayName();
        return displayName.getString();
    }

    @Override
    public String getUniqueId(MobEffectInstance ingredient, UidContext uidContext) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        return "effect:" + registryName;
    }

    @Override
    public String getModId(MobEffectInstance ingredient) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        if (registryName == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("effect.getRegistryName() returned null for: " + ingredientInfo);
        } else {
            return registryName.getNamespace();
        }
    }

    @Override
    public String getResourceId(MobEffectInstance ingredient) {
        ResourceLocation registryName = ingredient.getEffect().getRegistryName();
        if (registryName == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("ingredient.getRegistryName() returned null for: " + ingredientInfo);
        } else {
            return registryName.getPath();
        }
    }

    @Override
    public Iterable<Integer> getColors(MobEffectInstance ingredient) {
        return Collections.singletonList(ingredient.getEffect().getColor());
    }
    @Override
    public ItemStack getCheatItemStack(MobEffectInstance ingredient) {
        return PotionUtils.setCustomEffects(new ItemStack(Items.POTION),
                Collections.singletonList(ingredient));
    }

    @Override
    public MobEffectInstance copyIngredient(MobEffectInstance ingredient) {
        return new MobEffectInstance(ingredient.getEffect(), ingredient.getDuration(), ingredient.getAmplifier(),
                ingredient.isAmbient(), ingredient.isVisible(), ingredient.showIcon());
    }

    @Override
    public MobEffectInstance normalizeIngredient(MobEffectInstance ingredient) {
        return new MobEffectInstance(ingredient.getEffect(), 200);
    }

    @Override
    public String getErrorInfo(@Nullable MobEffectInstance ingredient) {
        if (ingredient == null) {
            return "null";
        } else {
            ToStringHelper toStringHelper = MoreObjects.toStringHelper(FluidStack.class);
            MobEffect effect = ingredient.getEffect();
            if (effect != null) {
                Component displayName = ingredient.getEffect().getDisplayName();
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
