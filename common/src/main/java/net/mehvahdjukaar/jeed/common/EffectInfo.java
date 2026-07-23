package net.mehvahdjukaar.jeed.common;

import com.google.common.base.Suppliers;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.data.EffectProvider;
import net.mehvahdjukaar.jeed.data.PotionProvider;
import net.mehvahdjukaar.jeed.data.ProviderManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public abstract class EffectInfo {

    private static final Supplier<Map<MobEffect, List<ItemStack>>> STATIC_CACHE = Suppliers.memoize(EffectInfo::buildStaticCache);

    protected final List<FormattedText> description;
    protected final MobEffectInstance effect;

    protected EffectInfo(MobEffectInstance effectInstance, List<FormattedText> description) {
        this.description = description;
        this.effect = effectInstance;
    }

    public List<FormattedText> getDescription() {
        return description;
    }

    public MobEffectInstance getEffect() {
        return effect;
    }


    private static Map<MobEffect, List<ItemStack>> buildStaticCache() {
        Map<MobEffect, List<ItemStack>> effectProvidingItems = new HashMap<>();

        //stews
        for (Block b : BuiltInRegistries.BLOCK) {
            if (b instanceof FlowerBlock flowerblock) {

                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);

                SuspiciousStewEffects effects = flowerblock.getSuspiciousEffects();
                stew.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effects);
                for (var e : effects.effects()) {
                    effectProvidingItems.computeIfAbsent(e.effect().value(),
                            s -> (new ItemStackList())).add(stew);
                }
            }
        }

        //foods
        for (Item i : BuiltInRegistries.ITEM) {
            Consumable consumable = i.getDefaultInstance().get(DataComponents.CONSUMABLE);
            if (consumable != null) {

                ItemStack foodItem = new ItemStack(i);
                for (ConsumeEffect consumeEffect : consumable.onConsumeEffects()) {
                    if (consumeEffect instanceof ApplyStatusEffectsConsumeEffect applyEffects) {
                        for (MobEffectInstance effect : applyEffects.effects()) {
                            effectProvidingItems.computeIfAbsent(effect.getEffect().value(),
                                    s -> (new ItemStackList())).add(foodItem);
                        }
                    }
                }
            }
        }

        //beacon
        for (var array : BeaconBlockEntity.BEACON_EFFECTS) {
            for (var e : array) {
                effectProvidingItems.computeIfAbsent(e.value(),
                        s -> (new ItemStackList())).add(Items.BEACON.getDefaultInstance());
            }
        }
        return effectProvidingItems;
    }

    public static List<Holder<MobEffect>> computeEffectProviders(MobEffect effect) {
        List<Holder<MobEffect>> list = new ArrayList<>();

        for (EffectProvider provider : ProviderManager.getEffectProviders()) {
            if (provider.matches(effect)) {
                provider.effectProviders().forEach(list::add);
            }
        }
        if (Jeed.sortIngredients()) {
            list.sort((o1, o2) -> ID_COMPARATOR.compare(o1.unwrapKey().get().identifier(),
                    o2.unwrapKey().get().identifier()));
        }
        return list;
    }

    public static List<Holder<Fluid>> computeFluidProvides(MobEffect effect) {
        List<Holder<Fluid>> list = new ArrayList<>();

        for (EffectProvider provider : ProviderManager.getEffectProviders()) {
            if (provider.matches(effect)) {
                provider.fluidProviders().forEach(list::add);
            }
        }
        if (Jeed.sortIngredients()) {
            list.sort((o1, o2) -> ID_COMPARATOR.compare(o1.unwrapKey().get().identifier(),
                    o2.unwrapKey().get().identifier()));
        }
        return list;
    }

    public static List<ItemStack> computeItemProviders(MobEffect effect) {

        ItemStackList list = new ItemStackList();

        for (EffectProvider provider : ProviderManager.getEffectProviders()) {
            if (provider.matches(effect)) {
                list.addAll(provider.providers());
            }
        }

        for (PotionProvider provider : ProviderManager.getPotionProviders()) {
            for (var potion : getPotions(provider)) {
                if (potion.value().getEffects().stream().anyMatch(e -> e.getEffect().value() == effect)) {
                    if (Jeed.ignoreDerivativePotions()) {
                        String path = potion.unwrapKey().get().identifier().getPath();
                        if (path.startsWith("long_") || path.startsWith("strong_")) {
                            continue;
                        }
                    }
                    for (ItemStack stack : provider.providers()) {
                        ItemStack copy = stack.copy();
                        PotionContents potionContents = new PotionContents(Optional.of(potion), Optional.empty(), List.of(), Optional.empty());
                        copy.set(DataComponents.POTION_CONTENTS, potionContents);
                        list.add(copy);
                    }
                }
            }
        }

        var stat = STATIC_CACHE.get().get(effect);
        if (stat != null) list.addAll(stat);

        if (Jeed.sortIngredients()) {
            list.sort((o1, o2) -> ID_COMPARATOR.compare(o1.typeHolder().unwrapKey().get().identifier(),
                    o2.typeHolder().unwrapKey().get().identifier()));
        }
        return list;
    }

    private static List<? extends Holder<net.minecraft.world.item.alchemy.Potion>> getPotions(PotionProvider provider) {
        return provider.potions().isEmpty() ? BuiltInRegistries.POTION.listElements().toList() : provider.potions();
    }

    /**
     * Groups stacks of the same item into one slot entry. Ingredients can no longer carry components, so slots hold
     * plain stack lists that each viewer cycles through.
     */
    public static List<List<ItemStack>> groupProviders(List<ItemStack> providers) {
        Map<Item, List<ItemStack>> map = new LinkedHashMap<>();
        for (ItemStack stack : providers) {
            map.computeIfAbsent(stack.getItem(), i -> new ArrayList<>()).add(stack);
        }
        return List.copyOf(map.values());
    }

    public static List<ItemStack> flatten(List<List<ItemStack>> groups) {
        return groups.stream().flatMap(List::stream).toList();
    }

    public static <T, I> List<I> divideIntoSlots(List<T> ingredients, Function<List<T>, I> mapper) {

        List<List<T>> slotContents = new ArrayList<>();

        for (int slotId = 0; slotId < ingredients.size(); slotId++) {

            int ind = slotId % (SLOTS_PER_ROW * ROWS);
            if (slotContents.size() <= ind) slotContents.add(new ArrayList<>());
            slotContents.get(ind).add(ingredients.get(slotId));
        }
        return slotContents.stream().map(mapper).toList();
    }

    private static class ItemStackList extends ArrayList<ItemStack> {

        public ItemStackList() {
            super();
        }

        @Override
        public boolean add(ItemStack stack) {
            if (stack.isEmpty()) return false;
            for (ItemStack thisStack : this) {
                if (ItemStack.matches(thisStack, stack)) {
                    return false;
                }
            }
            return super.add(stack);
        }

        @Override
        public boolean addAll(Collection<? extends ItemStack> stacks) {
            boolean changed = false;
            for (ItemStack stack : stacks) {
                changed |= this.add(stack);
            }
            return changed;
        }
    }

    public static Component getDescription(Holder<MobEffect> effect) {
        Identifier name = effect.unwrapKey().get().identifier();

        String descriptionKey = "effect." + name.getNamespace() + "." + name.getPath() + ".description";

        Component text = Component.translatable(descriptionKey);
        if (text.getString().equals(descriptionKey)) text = Component.translatable("jeed.description.missing");
        return text;
    }

    public static int getListHeight(List<?> inputs) {
        int listH = 0;
        if (Jeed.hasIngredientList() && !inputs.isEmpty()) {
            listH = Constants.MAX_BOX_HEIGHT;
            if (inputs.size() <= SLOTS_PER_ROW) {
                listH /= 2;
            }
        }
        return listH;
    }
}
