package net.mehvahdjukaar.jeed.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CodecUtil {

    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = RecordCodecBuilder.create((i) -> i.group(
                    Item.CODEC.fieldOf("item")
                            .forGetter(ItemStack::getItemHolder),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(ItemStack::getComponentsPatch))
            .apply(i, (holder, dataComponentPatch) -> new ItemStack(holder, 1, dataComponentPatch))
    );

    public static final Codec<List<ItemStack>> ITEM_PROVIDER = Codec.withAlternative(
            SINGLE_ITEM_CODEC.xmap(List::of, List::getFirst),
            Ingredient.CODEC.xmap(
                    ingredient -> ingredient.items().map(ItemStack::new).toList(),
                    stacks -> Ingredient.of(stacks.stream().map(ItemStack::getItem))
            )
    );
}
