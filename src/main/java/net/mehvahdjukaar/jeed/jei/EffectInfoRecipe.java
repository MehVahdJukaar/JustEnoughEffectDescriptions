package net.mehvahdjukaar.jeed.jei;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.util.MathUtil;
import net.mehvahdjukaar.jeed.recipes.EffectProviderRecipe;
import net.mehvahdjukaar.jeed.recipes.PotionProviderRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EffectInfoRecipe {

    private static final Lazy<Map<Effect, List<ItemStack>>> EFFECT_PROVIDERS_CACHE = Lazy.of(EffectInfoRecipe::buildEffectProviderCache);

    private static final int lineSpacing = 2;

    private final List<ITextProperties> description;
    private final EffectInstance effect;
    private final IIngredientType<EffectInstance> effectIngredientType;
    private final List<ItemStack> inputItems;

    private EffectInfoRecipe(EffectInstance effectInstance, IIngredientType<EffectInstance> ingredientType, List<ITextProperties> description) {
        this.description = description;
        this.effect = effectInstance;
        this.effectIngredientType = ingredientType;
        this.inputItems = getEffectProviders(effectInstance.getEffect());
    }

    private static Map<Effect, List<ItemStack>> buildEffectProviderCache() {

        Map<Effect, List<ItemStack>> effectProvidingItems = new HashMap<>();


        World world = Minecraft.getInstance().level;
        if(world != null){

            //effects
            List<EffectProviderRecipe> recipes = getRecipesOfType(world, r -> (r instanceof EffectProviderRecipe ?
                    ((EffectProviderRecipe)r) : null));

            for(EffectProviderRecipe p : recipes){
                effectProvidingItems.computeIfAbsent(p.getEffect(), i -> (new ItemStackList())).addAll(p.getProviders());
            }

            //potions
            List<PotionProviderRecipe> potionRecipes = getRecipesOfType(world, r -> (r instanceof PotionProviderRecipe ?
                    ((PotionProviderRecipe)r) : null));

            for(PotionProviderRecipe p : potionRecipes){
                for(ItemStack stack : p.getProviders()){

                    Collection<Potion> acceptablePotions = p.getPotions();
                    if(acceptablePotions.isEmpty()){
                        acceptablePotions = ForgeRegistries.POTION_TYPES.getValues();
                    }

                    for (Potion potion : acceptablePotions) {
                        ItemStack copy = stack.copy();
                        PotionUtils.setPotion(copy, potion);
                        for (EffectInstance effect : potion.getEffects()){
                            effectProvidingItems.computeIfAbsent(effect.getEffect(), i -> (new ItemStackList())).add(copy);
                        }
                    }
                }
            }

            //stew
            for (Block b : ForgeRegistries.BLOCKS) {
                if (b instanceof FlowerBlock) {

                    ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);

                    FlowerBlock flowerblock = (FlowerBlock) b;
                    Effect effect = flowerblock.getSuspiciousStewEffect();
                    SuspiciousStewItem.saveMobEffect(stew, effect, 200);

                    effectProvidingItems.computeIfAbsent(effect, i -> (new ItemStackList())).add(stew);

                }
            }

            //food
            for (Item i : ForgeRegistries.ITEMS) {
                Food food = i.getFoodProperties();
                if (food!=null) {

                    ItemStack foodItem = new ItemStack(i);
                    for (Pair<EffectInstance, Float> pair : food.getEffects()){
                        effectProvidingItems.computeIfAbsent(pair.getFirst().getEffect(), s -> (new ItemStackList())).add(foodItem);
                    }
                }
            }

            //beacon
            for (Effect[] array : BeaconTileEntity.BEACON_EFFECTS) {
                for (Effect e : array) {
                    effectProvidingItems.computeIfAbsent(e, s -> (new ItemStackList())).add(Items.BEACON.getDefaultInstance());
                }
            }

        }
        return effectProvidingItems;
    }


    private static <T> List<T> getRecipesOfType(World world, Function<IRecipe<?>,T> function){
        return world.getRecipeManager().getRecipes().stream()
                .map(function).filter(Objects::nonNull).collect(Collectors.toList());

    }

    public List<ItemStack> getInputItems() {
        return this.inputItems.stream().filter(s ->!s.isEmpty()).collect(Collectors.toList());
    }

    private static NonNullList<ItemStack> getEffectProviders (Effect effect) {
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll (EFFECT_PROVIDERS_CACHE.get().getOrDefault(effect, (new ItemStackList())));
        return list;
    }

    public static List<EffectInfoRecipe> create(EffectInstance ingredient, IIngredientType<EffectInstance> ingredientType, String descriptionKey) {
        ITextComponent text = new TranslationTextComponent(descriptionKey);
        if(text.getString().equals(descriptionKey)) text = new TranslationTextComponent("jeed.description.missing");

        return create(ingredient, ingredientType, text);
    }

    public static List<EffectInfoRecipe> create(EffectInstance ingredient, IIngredientType<EffectInstance> ingredientType, ITextComponent descriptionComponent) {
        List<EffectInfoRecipe> recipes = new ArrayList<>();
        List<ITextProperties> descriptionLines = expandNewlines(descriptionComponent);
        descriptionLines = wrapDescriptionLines(descriptionLines);
        final int lineCount = descriptionLines.size();

        Minecraft minecraft = Minecraft.getInstance();
        final int maxLinesPerPage = (EffectRecipeCategory.recipeHeight - 80) / (minecraft.font.lineHeight + lineSpacing);
        final int pageCount = MathUtil.divideCeil(lineCount, maxLinesPerPage);
        for (int i = 0; i < pageCount; i++) {
            int startLine = i * maxLinesPerPage;
            int endLine = Math.min((i + 1) * maxLinesPerPage, lineCount);
            List<ITextProperties> description = descriptionLines.subList(startLine, endLine);
            EffectInfoRecipe recipe = new EffectInfoRecipe(ingredient, ingredientType, description);
            recipes.add(recipe);
        }

        return recipes;
    }

    private static List<ITextProperties> expandNewlines(ITextComponent... descriptionComponents) {
        List<ITextProperties> descriptionLinesExpanded = new ArrayList<>();
        for (ITextComponent descriptionLine : descriptionComponents) {
            ExpandNewLineTextAcceptor newLineTextAcceptor = new ExpandNewLineTextAcceptor();
            descriptionLine.visit(newLineTextAcceptor, Style.EMPTY);
            newLineTextAcceptor.addLinesTo(descriptionLinesExpanded);
        }
        return descriptionLinesExpanded;
    }

    private static List<ITextProperties> wrapDescriptionLines(List<ITextProperties> descriptionLines) {
        Minecraft minecraft = Minecraft.getInstance();
        List<ITextProperties> descriptionLinesWrapped = new ArrayList<>();
        for (ITextProperties descriptionLine : descriptionLines) {
            List<ITextProperties> textLines = minecraft.font.getSplitter().splitLines(descriptionLine, EffectRecipeCategory.recipeWidth, Style.EMPTY);
            descriptionLinesWrapped.addAll(textLines);
        }
        return descriptionLinesWrapped;
    }

    public List<ITextProperties> getDescription() {
        return description;
    }

    public IIngredientType<EffectInstance> getEffectIngredientType() {
        return effectIngredientType;
    }

    public EffectInstance getEffect() {
        return effect;
    }

    private static class ExpandNewLineTextAcceptor implements ITextProperties.IStyledTextAcceptor<Void> {

        private final List<ITextProperties> lines = new ArrayList<>();

        @Nullable
        private IFormattableTextComponent lastComponent;

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
                        lines.add(StringTextComponent.EMPTY);
                    }
                    continue;
                }
                StringTextComponent textComponent = new StringTextComponent(s);
                textComponent.setStyle(style);
                if (lastComponent != null) {
                    //If we already have a component that we want to continue with
                    if (i == 0) {
                        // and we are the first line, add ourselves to the last component
                        if (!lastComponent.getStyle().isEmpty() && !lastComponent.getStyle().equals(style)) {
                            //If it has a style and the style is different from the style the text component
                            // we are adding has add the last component as a sibling to an empty unstyled
                            // component so that we don't cause the styling to leak into the component we are adding
                            lastComponent = new StringTextComponent("").append(lastComponent);
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

        public void addLinesTo(List<ITextProperties> descriptionLinesExpanded) {
            descriptionLinesExpanded.addAll(lines);
            if (lastComponent != null) {
                descriptionLinesExpanded.add(lastComponent);
            }
        }
    }

    private static class ItemStackList extends ArrayList<ItemStack>{

        public ItemStackList(){
            super();
        }

        public ItemStackList(Effect ignored){
            super();
        }

        @Override
        public boolean add(ItemStack stack) {
            if(stack.isEmpty()) return false;
            for(ItemStack thisStack : this){
                if(ItemStack.matches(thisStack, stack)){
                    return false;
                }
            }
            return super.add(stack);
        }
    }
}