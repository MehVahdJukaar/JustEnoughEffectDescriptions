package net.mehvahdjukaar.jeed.common;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.*;
import java.util.function.Supplier;

public abstract class EffectInfo {

    private static final Supplier<Map<MobEffect, List<ItemStack>>> STATIC_CACHE = Suppliers.memoize(EffectInfo::buildStaticCache);

    protected final List<FormattedText> description;
    protected final MobEffectInstance effect;
    protected final List<ItemStack> inputItems;

    protected EffectInfo(MobEffectInstance effectInstance, List<ItemStack> input, List<FormattedText> description) {
        this.description = description;
        this.effect = effectInstance;
        this.inputItems = input;
    }

    public static final Comparator<ItemStack> COMPARATOR = (o1, o2) -> {
        var r1 = Registry.ITEM.getKey(o1.getItem());
        var r2 = Registry.ITEM.getKey(o2.getItem());
        int i = r1.getNamespace().compareTo(r2.getNamespace());
        if (i == 0) {
            i = r1.getPath().compareTo(r1.getPath());
            if(i == 0){
                return o1.getDisplayName().getString().compareTo(o2.getDisplayName().getString());
            }
        }
        return i;
    };

    public List<ItemStack> getInputItems() {
        return inputItems;
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
        for (Block b : Registry.BLOCK) {
            if (b instanceof FlowerBlock flowerblock) {

                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);

                MobEffect effect = flowerblock.getSuspiciousStewEffect();
                SuspiciousStewItem.saveMobEffect(stew, effect, 200);

                effectProvidingItems.computeIfAbsent(effect, i -> (new ItemStackList())).add(stew);
            }
        }

        //foods
        for (Item i : Registry.ITEM) {
            FoodProperties food = i.getFoodProperties();
            if (food != null) {

                ItemStack foodItem = new ItemStack(i);
                for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
                    effectProvidingItems.computeIfAbsent(pair.getFirst().getEffect(), s -> (new ItemStackList())).add(foodItem);
                }
            }
        }

        //beacon
        for (MobEffect[] array : BeaconBlockEntity.BEACON_EFFECTS) {
            for (MobEffect e : array) {
                effectProvidingItems.computeIfAbsent(e, s -> (new ItemStackList())).add(Items.BEACON.getDefaultInstance());
            }
        }
        return effectProvidingItems;
    }

    public static List<ItemStack> computeEffectProviders(MobEffect effect) {

        ItemStackList list = new ItemStackList();

        Level world = Minecraft.getInstance().level;
        if (world != null) {

            //effects
            List<EffectProviderRecipe> recipes = world.getRecipeManager()
                    .getAllRecipesFor(Jeed.getEffectProviderType());

            for (EffectProviderRecipe p : recipes) {
                for (var e : p.getEffects()) {
                    if (e == effect) {
                        for (var i : p.getIngredients()) {
                            list.addAll(List.of(i.getItems()));
                        }
                    }
                }
            }

            //potions
            List<PotionProviderRecipe> potionRecipes = world.getRecipeManager()
                    .getAllRecipesFor(Jeed.getPotionProviderType());

            for (PotionProviderRecipe p : potionRecipes) {
                Collection<Potion> acceptablePotions = p.getPotions();
                if (acceptablePotions.isEmpty()) {
                    acceptablePotions = Registry.POTION.stream().toList();
                }
                for (Potion potion : acceptablePotions) {
                    if (potion.getEffects().stream().anyMatch(e -> e.getEffect() == effect)) {
                        for (var ing : p.getIngredients()) {
                            for (var stack : ing.getItems()) {
                                ItemStack copy = stack.copy();
                                PotionUtils.setPotion(copy, potion);
                                list.add(copy);
                            }
                        }
                    }
                }
            }
        }

        var stat = STATIC_CACHE.get().get(effect);
        if (stat != null) list.addAll(stat);

        list.sort(COMPARATOR);
        return list;
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
    }

    public static Component getDescription(MobEffect effect) {
        ResourceLocation name = Registry.MOB_EFFECT.getKey(effect);

        String descriptionKey = "effect." + name.getNamespace() + "." + name.getPath() + ".description";

        Component text = Component.translatable(descriptionKey);
        if (text.getString().equals(descriptionKey)) text = Component.translatable("jeed.description.missing");
        return text;
    }

    public static int getListHeight(List<ItemStack> inputs) {
        int listH = 0;
        if (Jeed.hasIngredientList() && !inputs.isEmpty()) {
            listH = EffectCategory.MAX_BOX_HEIGHT;
            if (inputs.size() <= EffectCategory.SLOTS_PER_ROW) {
                listH /= 2;
            }
        }
        return listH;
    }
}