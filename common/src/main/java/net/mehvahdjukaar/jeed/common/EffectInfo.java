package net.mehvahdjukaar.jeed.common;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectRecipeCategory;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class EffectInfo {

    private static final Supplier<Map<MobEffect, List<ItemStack>>> STATIC_CACHE = Suppliers.memoize(EffectInfo::buildStaticCache);

    private static final int LINE_SPACING = 2;

    protected final List<FormattedText> description;
    protected final MobEffectInstance effect;
    protected final List<ItemStack> inputItems;

    protected EffectInfo(MobEffectInstance effectInstance, List<FormattedText> description) {
        this.description = description;
        this.effect = effectInstance;
        this.inputItems = computeEffectProviders(effectInstance.getEffect()).stream().sorted().toList();
    }

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

    private static List<ItemStack> computeEffectProviders(MobEffect effect) {

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

        return list;
    }

    protected static <T extends EffectInfo> List<T> create(
            MobEffect effect, BiFunction<MobEffectInstance, List<FormattedText>, T> constructor) {

        ResourceLocation name = Registry.MOB_EFFECT.getKey(effect);

        String descriptionKey =  "effect." + name.getNamespace() + "." +
                name.getPath() + ".description";

        Component text = Component.translatable(descriptionKey);
        if (text.getString().equals(descriptionKey)) text = Component.translatable("jeed.description.missing");

        List<T> recipes = new ArrayList<>();
        List<FormattedText> descriptionLines = expandNewlines(text);
        descriptionLines = wrapDescriptionLines(descriptionLines);
        final int lineCount = descriptionLines.size();

        Minecraft minecraft = Minecraft.getInstance();
        final int maxLinesPerPage = (EffectRecipeCategory.RECIPE_HEIGHT - (Jeed.hasIngredientList() ? 80 : 0)) / (minecraft.font.lineHeight + LINE_SPACING);
        final int pageCount = divideCeil(lineCount, maxLinesPerPage);
        for (int i = 0; i < pageCount; i++) {
            int startLine = i * maxLinesPerPage;
            int endLine = Math.min((i + 1) * maxLinesPerPage, lineCount);
            List<FormattedText> description = descriptionLines.subList(startLine, endLine);
            T recipe = constructor.apply(new MobEffectInstance(effect), description);
            recipes.add(recipe);
        }

        return recipes;
    }

    private static int divideCeil(int numerator, int denominator) {
        return (int) Math.ceil((float) numerator / (float) denominator);
    }

    private static List<FormattedText> expandNewlines(Component... descriptionComponents) {
        List<FormattedText> descriptionLinesExpanded = new ArrayList<>();
        for (Component descriptionLine : descriptionComponents) {
            ExpandNewLineTextAcceptor newLineTextAcceptor = new ExpandNewLineTextAcceptor();
            descriptionLine.visit(newLineTextAcceptor, Style.EMPTY);
            newLineTextAcceptor.addLinesTo(descriptionLinesExpanded);
        }
        return descriptionLinesExpanded;
    }

    private static List<FormattedText> wrapDescriptionLines(List<FormattedText> descriptionLines) {
        Minecraft minecraft = Minecraft.getInstance();
        List<FormattedText> descriptionLinesWrapped = new ArrayList<>();
        for (FormattedText descriptionLine : descriptionLines) {
            List<FormattedText> textLines = minecraft.font.getSplitter().splitLines(descriptionLine, EffectRecipeCategory.RECIPE_WIDTH, Style.EMPTY);
            descriptionLinesWrapped.addAll(textLines);
        }
        return descriptionLinesWrapped;
    }


    private static class ExpandNewLineTextAcceptor implements FormattedText.StyledContentConsumer<Void> {

        private final List<FormattedText> lines = new ArrayList<>();

        @Nullable
        private MutableComponent lastComponent;

        @Override
        public Optional<Void> accept(Style style, String line) {
            String[] descriptionLineExpanded = line.split("\\\\n");
            for (int i = 0; i < descriptionLineExpanded.length; i++) {
                String s = descriptionLineExpanded[i];
                if (s.isEmpty()) {
                    //If the string is empty
                    if (i == 0 && lastComponent != null) {
                        // and we are the first string (for example from a string \nTest)
                        // and we had a last component (we are a variable in a translation string)
                        // add our last component as is and reset it
                        lines.add(lastComponent);
                        lastComponent = null;
                    } else {
                        //Otherwise just add the empty line
                        lines.add(Component.EMPTY);
                    }
                    continue;
                }
                MutableComponent textComponent = Component.literal(s);
                textComponent.setStyle(style);
                if (lastComponent != null) {
                    //If we already have a component that we want to continue with
                    if (i == 0) {
                        // and we are the first line, add ourselves to the last component
                        if (!lastComponent.getStyle().isEmpty() && !lastComponent.getStyle().equals(style)) {
                            //If it has a style and the style is different from the style the text component
                            // we are adding has add the last component as a sibling to an empty unstyled
                            // component so that we don't cause the styling to leak into the component we are adding
                            lastComponent = Component.literal("").append(lastComponent);
                        }
                        lastComponent.append(textComponent);
                        continue;
                    } else {
                        // otherwise if we aren't the first line, add the old component to our list of lines
                        lines.add(lastComponent);
                        lastComponent = null;
                    }
                }
                if (i == descriptionLineExpanded.length - 1) {
                    //If we are the last line we are adding, persist the text component
                    lastComponent = textComponent;
                } else {
                    //Otherwise add it to our list of lines
                    lines.add(textComponent);
                }
            }
            return Optional.empty();
        }

        public void addLinesTo(List<FormattedText> descriptionLinesExpanded) {
            descriptionLinesExpanded.addAll(lines);
            if (lastComponent != null) {
                descriptionLinesExpanded.add(lastComponent);
            }
        }
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
}