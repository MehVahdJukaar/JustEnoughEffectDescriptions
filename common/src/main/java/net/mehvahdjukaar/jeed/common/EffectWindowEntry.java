package net.mehvahdjukaar.jeed.common;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public abstract class EffectWindowEntry {

    private static final Supplier<Map<MobEffect, List<ItemStack>>> STATIC_CACHE = Suppliers.memoize(EffectWindowEntry::buildStaticCache);

    protected final List<FormattedText> description;
    protected final MobEffectInstance effect;

    protected EffectWindowEntry(MobEffectInstance effectInstance, List<FormattedText> description) {
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

                MobEffect effect = flowerblock.getSuspiciousEffect();
                SuspiciousStewItem.saveMobEffect(stew, effect, 200);

                effectProvidingItems.computeIfAbsent(effect, i -> (new ItemStackList())).add(stew);
            }
        }

        //foods
        for (Item i : BuiltInRegistries.ITEM) {
            FoodProperties food = i.getFoodProperties();
            if (food != null) {

                ItemStack foodItem = new ItemStack(i);
                for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
                    MobEffectInstance first = pair.getFirst();
                    if (first != null) { //why is this nullable?? vanilla never puts null here nor its marked as such
                        effectProvidingItems.computeIfAbsent(first.getEffect(), s -> (new ItemStackList())).add(foodItem);
                    }
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

    public static List<Holder<MobEffect>> computeEffectProviders(MobEffect effect) {
        List<Holder<MobEffect>> list = new ArrayList<>();

        Level world = Minecraft.getInstance().level;
        if (world != null) {

            //effects
            var recipes = world.getRecipeManager()
                    .getAllRecipesFor(Jeed.getEffectProviderType());

            for (var recipeHolder : recipes) {
                EffectProviderRecipe recipe = recipeHolder.value();
                for (var e : recipe.getEffects()) {
                    if (e.value() == effect) {
                        recipe.effectProviders().forEach(list::add);
                    }
                }
            }
        }
        return list;
    }

    public static List<Holder<Fluid>> computeFluidProvides(MobEffect effect){
        List<Holder<Fluid>> list = new ArrayList<>();

        Level world = Minecraft.getInstance().level;
        if (world != null) {

            //effects
            var recipes = world.getRecipeManager()
                    .getAllRecipesFor(Jeed.getEffectProviderType());

            for (var recipeHolder : recipes) {
                EffectProviderRecipe recipe = recipeHolder.value();
                for (var e : recipe.getEffects()) {
                    if (e.value() == effect) {
                        recipe.fluidProviders().forEach(list::add);
                    }
                }
            }
        }
        return list;
    }

    public static List<ItemStack> computeItemProviders(MobEffect effect) {

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
                    acceptablePotions = BuiltInRegistries.POTION.stream().toList();
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

        return list;
    }


    public static List<Ingredient> groupIngredients(List<ItemStack> ingredients) {
        Map<Item, Ingredient> map = new HashMap<>();
        for (ItemStack stack : ingredients) {
            map.merge(stack.getItem(), Ingredient.of(stack), EffectWindowEntry::mergeIngredients);
        }
        var entryList = sortIngredients(map);

        // Create a new LinkedHashMap and insert sorted entries
        List<Ingredient> list = new ArrayList<>();
        for (var entry : entryList) {
            list.add(entry.getValue());
        }
        return list;
    }

    private static Ingredient mergeIngredients(Ingredient ingredient, Ingredient ingredient1) {
        return mergeIngredients(List.of(ingredient, ingredient1));
    }

    public static Ingredient mergeIngredients(List<Ingredient> ingredients) {
        List<ItemStack> l = new ArrayList<>();
        for (Ingredient i : ingredients) {
            l.addAll(Arrays.stream(i.getItems()).toList());
        }
        return Ingredient.of(l.toArray(new ItemStack[0]));
    }

    private static @NotNull ArrayList<Map.Entry<Item, Ingredient>> sortIngredients(Map<Item, Ingredient> map) {
        var entryList = new ArrayList<>(map.entrySet());
        entryList.sort((a, b) -> ID_COMPARATOR.compare(BuiltInRegistries.ITEM.getKey(a.getKey()),
                BuiltInRegistries.ITEM.getKey(b.getKey())));
        return entryList;
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
    }

    public static Component getDescription(MobEffect effect) {
        ResourceLocation name = BuiltInRegistries.MOB_EFFECT.getKey(effect);

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