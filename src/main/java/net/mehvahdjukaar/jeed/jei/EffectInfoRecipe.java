package net.mehvahdjukaar.jeed.jei;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class EffectInfoRecipe {

    private static final Lazy<Map<MobEffect, List<ItemStack>>> EFFECT_PROVIDERS_CACHE = Lazy.of(EffectInfoRecipe::buildEffectProviderCache);

    private static final int LINE_SPACING = 2;

    private final List<FormattedText> description;
    private final MobEffectInstance effect;
    private final IIngredientType<MobEffectInstance> effectIngredientType;
    private final List<ItemStack> inputItems;

    private EffectInfoRecipe(MobEffectInstance effectInstance, IIngredientType<MobEffectInstance> ingredientType, List<FormattedText> description) {
        this.description = description;
        this.effect = effectInstance;
        this.effectIngredientType = ingredientType;
        this.inputItems = getEffectProviders(effectInstance.getEffect());
    }

    private static Map<MobEffect, List<ItemStack>> buildEffectProviderCache() {

        Map<MobEffect, List<ItemStack>> effectProvidingItems = new HashMap<>();


        Level world = Minecraft.getInstance().level;
        if (world != null) {

            //effects
            List<EffectProviderRecipe> recipes = getRecipesOfType(world, r -> (r instanceof EffectProviderRecipe ep ?
                    ep : null));

            for (EffectProviderRecipe p : recipes) {
                p.getEffects().forEach(e ->
                        effectProvidingItems.computeIfAbsent(e, i -> (new ItemStackList())).addAll(p.getProviders()));
            }

            //potions
            List<PotionProviderRecipe> potionRecipes = getRecipesOfType(world, r -> (r instanceof PotionProviderRecipe pp ?
                    pp : null));

            for (PotionProviderRecipe p : potionRecipes) {
                for (ItemStack stack : p.getProviders()) {

                    Collection<Potion> acceptablePotions = p.getPotions();
                    if (acceptablePotions.isEmpty()) {
                        acceptablePotions = ForgeRegistries.POTIONS.getValues();
                    }

                    for (Potion potion : acceptablePotions) {
                        ItemStack copy = stack.copy();
                        PotionUtils.setPotion(copy, potion);
                        for (MobEffectInstance effect : potion.getEffects()) {
                            effectProvidingItems.computeIfAbsent(effect.getEffect(), i -> (new ItemStackList())).add(copy);
                        }
                    }
                }
            }

            //stews
            for (Block b : ForgeRegistries.BLOCKS) {
                if (b instanceof FlowerBlock flowerblock) {

                    ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);

                    MobEffect effect = flowerblock.getSuspiciousStewEffect();
                    SuspiciousStewItem.saveMobEffect(stew, effect, 200);

                    effectProvidingItems.computeIfAbsent(effect, i -> (new ItemStackList())).add(stew);

                }
            }

            //foods
            for (Item i : ForgeRegistries.ITEMS) {
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

        }
        return effectProvidingItems;
    }


    private static <T> List<T> getRecipesOfType(Level world, Function<Recipe<?>, T> function) {
        return world.getRecipeManager().getRecipes().stream()
                .map(function).filter(Objects::nonNull).toList();

    }

    public List<ItemStack> getInputItems() {
        IIngredientVisibility ingredientVisibility = JEIPlugin.JEI_INGREDIENT_VISIBILITY;
        return inputItems.stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> ingredientVisibility.isIngredientVisible(VanillaTypes.ITEM_STACK, s))
                 .toList();
    }

    private static NonNullList<ItemStack> getEffectProviders(MobEffect effect) {
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(EFFECT_PROVIDERS_CACHE.get().getOrDefault(effect, (new ItemStackList())));
        return list;
    }

    public static List<EffectInfoRecipe> create(MobEffectInstance ingredient, IIngredientType<MobEffectInstance> ingredientType, String descriptionKey) {
        Component text = Component.translatable(descriptionKey);
        if (text.getString().equals(descriptionKey)) text = Component.translatable("jeed.description.missing");

        return create(ingredient, ingredientType, text);
    }

    public static List<EffectInfoRecipe> create(MobEffectInstance ingredient, IIngredientType<MobEffectInstance> ingredientType, Component descriptionComponent) {
        List<EffectInfoRecipe> recipes = new ArrayList<>();
        List<FormattedText> descriptionLines = expandNewlines(descriptionComponent);
        descriptionLines = wrapDescriptionLines(descriptionLines);
        final int lineCount = descriptionLines.size();

        Minecraft minecraft = Minecraft.getInstance();
        final int maxLinesPerPage = (EffectRecipeCategory.RECIPE_HEIGHT - (Jeed.INGREDIENTS_LIST.get() ? 80 : 0)) / (minecraft.font.lineHeight + LINE_SPACING);
        final int pageCount = divideCeil(lineCount, maxLinesPerPage);
        for (int i = 0; i < pageCount; i++) {
            int startLine = i * maxLinesPerPage;
            int endLine = Math.min((i + 1) * maxLinesPerPage, lineCount);
            List<FormattedText> description = descriptionLines.subList(startLine, endLine);
            EffectInfoRecipe recipe = new EffectInfoRecipe(ingredient, ingredientType, description);
            recipes.add(recipe);
        }

        return recipes;
    }

    public static int divideCeil(int numerator, int denominator) {
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

    public List<FormattedText> getDescription() {
        return description;
    }

    public IIngredientType<MobEffectInstance> getEffectIngredientType() {
        return effectIngredientType;
    }

    public MobEffectInstance getEffect() {
        return effect;
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

        public ItemStackList(MobEffect ignored) {
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