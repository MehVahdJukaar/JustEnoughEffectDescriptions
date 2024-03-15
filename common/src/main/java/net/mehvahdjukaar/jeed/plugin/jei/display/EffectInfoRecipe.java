package net.mehvahdjukaar.jeed.plugin.jei.display;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EffectInfoRecipe extends EffectInfo {

    public static final RecipeType<EffectInfoRecipe> TYPE = RecipeType.create(Jeed.MOD_ID, "effect_info", EffectInfoRecipe.class);

    protected EffectInfoRecipe(MobEffectInstance effectInstance, List<ItemStack> input, List<FormattedText> description) {
        super(effectInstance, input, description);
    }

    @Override
    public List<ItemStack> getInputItems() {
        IIngredientVisibility ingredientVisibility = JEIPlugin.JEI_INGREDIENT_VISIBILITY;
        return inputItems.stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> ingredientVisibility.isIngredientVisible(VanillaTypes.ITEM_STACK, s))
                .toList();
    }

    public static List<EffectInfoRecipe> create(MobEffect effect) {
        Minecraft minecraft = Minecraft.getInstance();
        Component text = getDescription(effect);
        List<ItemStack> inputs = computeEffectProviders(effect);

        int listH = getListHeight(inputs);

        List<EffectInfoRecipe> recipes = new ArrayList<>();
        List<FormattedText> descriptionLines = expandNewlines(text);
        descriptionLines = wrapDescriptionLines(descriptionLines);
        final int lineCount = descriptionLines.size();


        final int maxLinesPerPage = (EffectCategory.RECIPE_HEIGHT - 35 - listH) / (minecraft.font.lineHeight + EffectCategory.LINE_SPACING);
        final int pageCount = divideCeil(lineCount, maxLinesPerPage);
        for (int i = 0; i < pageCount; i++) {
            int startLine = i * maxLinesPerPage;
            int endLine = Math.min((i + 1) * maxLinesPerPage, lineCount);
            List<FormattedText> description = descriptionLines.subList(startLine, endLine);
            EffectInfoRecipe recipe = new EffectInfoRecipe(new MobEffectInstance(effect), inputs, description);
            recipes.add(recipe);
        }

        return recipes;
    }

    private static int divideCeil(int numerator, int denominator) {
        return (int) Math.ceil((float) numerator / (float) denominator);
    }

    private static List<FormattedText> expandNewlines(Component descriptionComponents) {
        List<FormattedText> descriptionLinesExpanded = new ArrayList<>();
        ExpandNewLineTextAcceptor newLineTextAcceptor = new ExpandNewLineTextAcceptor();
        descriptionComponents.visit(newLineTextAcceptor, Style.EMPTY);
        newLineTextAcceptor.addLinesTo(descriptionLinesExpanded);
        return descriptionLinesExpanded;
    }

    private static List<FormattedText> wrapDescriptionLines(List<FormattedText> descriptionLines) {
        Minecraft minecraft = Minecraft.getInstance();
        List<FormattedText> descriptionLinesWrapped = new ArrayList<>();
        for (FormattedText descriptionLine : descriptionLines) {
            List<FormattedText> textLines = minecraft.font.getSplitter().splitLines(descriptionLine, Jeed.PLUGIN.getMaxTextWidth(), Style.EMPTY);
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


}