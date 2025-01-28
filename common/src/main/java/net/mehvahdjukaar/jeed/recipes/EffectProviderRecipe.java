package net.mehvahdjukaar.jeed.recipes;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.CodecUtil;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record EffectProviderRecipe(Optional<Holder<MobEffect>> effect,
                                   NonNullList<Ingredient> providers,
                                   HolderSet<MobEffect> effectProviders,
                                   HolderSet<Fluid> fluidProviders)
        implements Recipe<SingleRecipeInput> {

    @Override
    public String getGroup() {
        return "effect_provider";
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return providers;
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Jeed.getEffectProviderSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return Jeed.getEffectProviderType();
    }

    public Collection<? extends Holder<MobEffect>> getEffects() {
        return effect.isEmpty() ? BuiltInRegistries.MOB_EFFECT.holders().toList() : Collections.singletonList(effect.get());
    }


    public static class Serializer implements RecipeSerializer<EffectProviderRecipe> {



        private static final MapCodec<EffectProviderRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("effect").forGetter((shapelessRecipe) -> shapelessRecipe.effect),
                CodecUtil.INGREDIENT_WITH_TAG.listOf().optionalFieldOf("providers", List.of()).flatXmap((list) -> {
                    Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                }, DataResult::success).forGetter((shapelessRecipe) -> shapelessRecipe.providers),
                RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).optionalFieldOf("effect_providers", HolderSet.empty()).forGetter((r) -> r.effectProviders),
                RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluid_providers", HolderSet.empty()).forGetter((r) -> r.fluidProviders)
        ).apply(instance, EffectProviderRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EffectProviderRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), EffectProviderRecipe::effect,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)), EffectProviderRecipe::providers,
                ByteBufCodecs.holderSet(Registries.MOB_EFFECT), EffectProviderRecipe::effectProviders,
                ByteBufCodecs.holderSet(Registries.FLUID), EffectProviderRecipe::fluidProviders,
                EffectProviderRecipe::new);

        @Override
        public MapCodec<EffectProviderRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EffectProviderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
