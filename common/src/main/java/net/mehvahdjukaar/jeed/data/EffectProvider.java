package net.mehvahdjukaar.jeed.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.jeed.common.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Optional;

/**
 * Lists what grants a given effect. An empty {@code effect} means "every effect".
 */
public record EffectProvider(Optional<Holder<MobEffect>> effect,
                             List<ItemStack> providers,
                             HolderSet<MobEffect> effectProviders,
                             HolderSet<Fluid> fluidProviders) implements ProviderEntry {

    public static final MapCodec<EffectProvider> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryFixedCodec.create(Registries.MOB_EFFECT).optionalFieldOf("effect").forGetter(EffectProvider::effect),
            CodecUtil.ITEM_PROVIDER.listOf().optionalFieldOf("providers", List.of())
                    .xmap(l -> l.stream().flatMap(List::stream).toList(), List::of)
                    .forGetter(EffectProvider::providers),
            RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).optionalFieldOf("effect_providers", HolderSet.empty())
                    .forGetter(EffectProvider::effectProviders),
            RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluid_providers", HolderSet.empty())
                    .forGetter(EffectProvider::fluidProviders)
    ).apply(i, EffectProvider::new));

    @Override
    public Identifier type() {
        return EFFECT_PROVIDER;
    }

    public boolean matches(MobEffect effect) {
        return this.effect.map(e -> e.value() == effect).orElse(true);
    }
}
