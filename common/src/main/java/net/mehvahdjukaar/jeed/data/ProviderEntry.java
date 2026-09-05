package net.mehvahdjukaar.jeed.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.resources.Identifier;

import java.util.Map;

public sealed interface ProviderEntry permits EffectProvider, PotionProvider {

    Identifier EFFECT_PROVIDER = Jeed.res("effect_provider");
    Identifier POTION_PROVIDER = Jeed.res("potion_provider");

    Map<Identifier, MapCodec<? extends ProviderEntry>> TYPES = Map.of(
            EFFECT_PROVIDER, EffectProvider.CODEC,
            POTION_PROVIDER, PotionProvider.CODEC
    );

    Codec<ProviderEntry> CODEC = Identifier.CODEC.dispatch("type", ProviderEntry::type, TYPES::get);

    Identifier type();

    default MapCodec<? extends ProviderEntry> codec() {
        return TYPES.get(this.type());
    }
}
