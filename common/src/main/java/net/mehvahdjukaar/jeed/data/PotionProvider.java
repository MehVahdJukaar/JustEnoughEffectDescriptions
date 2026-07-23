package net.mehvahdjukaar.jeed.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.jeed.common.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;

/**
 * Items that can hold any potion. An empty {@code potions} list means "every potion".
 */
public record PotionProvider(List<Holder<Potion>> potions,
                             List<ItemStack> providers) implements ProviderEntry {

    public static final MapCodec<PotionProvider> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryFixedCodec.create(Registries.POTION).listOf().optionalFieldOf("potions", List.of())
                    .forGetter(PotionProvider::potions),
            CodecUtil.ITEM_PROVIDER.listOf().fieldOf("providers")
                    .xmap(l -> l.stream().flatMap(List::stream).toList(), List::of)
                    .forGetter(PotionProvider::providers)
    ).apply(i, PotionProvider::new));

    @Override
    public Identifier type() {
        return POTION_PROVIDER;
    }
}
