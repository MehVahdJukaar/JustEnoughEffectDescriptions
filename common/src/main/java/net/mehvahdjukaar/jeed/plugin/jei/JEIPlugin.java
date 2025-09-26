package net.mehvahdjukaar.jeed.plugin.jei;

import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectInfoRecipeCategory;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceHelper;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * Author: MehVahdJukaar
 */
@REIPluginCompatIgnore
@JeiPlugin
public class JEIPlugin implements IModPlugin, IPlugin {

    private static final ResourceLocation ID = Jeed.res("jei_plugin");

    public static final IIngredientType<MobEffectInstance> EFFECT_INGREDIENT_TYPE = () -> MobEffectInstance.class;

    public static IJeiRuntime JEI_RUNTIME;
    public static IJeiHelpers JEI_HELPERS;
    public static IIngredientVisibility JEI_INGREDIENT_VISIBILITY;

    public JEIPlugin() {
        if (Jeed.EMI) return;
        Jeed.PLUGIN = this;
    }

    @Override
    public boolean rendersTooltips() {
        return true;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (Jeed.EMI || Jeed.REI) return;

        registry.addRecipeCategories(new EffectInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        if (Jeed.EMI || Jeed.REI) return;

        registration.register(EFFECT_INGREDIENT_TYPE,
                Jeed.getEffectList().stream().map(MobEffectInstance::new).toList(),
                new EffectInstanceHelper(), EffectInstanceRenderer.INSTANCE,
                MobEffectInstance.CODEC);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (Jeed.EMI || Jeed.REI) return;

        JEI_INGREDIENT_VISIBILITY = registry.getJeiHelpers().getIngredientVisibility();
        JEI_HELPERS = registry.getJeiHelpers();

        for (var e : Jeed.getEffectList()) {
            List<EffectInfoRecipe> recipes = EffectInfoRecipe.create(e);
            registry.addRecipes(EffectInfoRecipe.TYPE, recipes);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (Jeed.EMI || Jeed.REI) return;

        for (var e : ScreenExtensionsHandler.EXTENSIONS.entrySet()) {
            var screenClass = (Class<AbstractContainerScreen<?>>) e.getKey();
            var effect = (IEffectScreenExtension<AbstractContainerScreen<?>>) e.getValue();
            var extension = new ScreenExtension<>(effect);

            registration.addGuiContainerHandler(screenClass, extension);
        }
    }

    public record ScreenExtension<T extends AbstractContainerScreen<?>>
            (IEffectScreenExtension<T> ext) implements IGuiContainerHandler<T> {


        @Override
        public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(
                IClickableIngredientFactory builder, T containerScreen, double mouseX, double mouseY) {
            MobEffectInstance v = ext.getEffectAtPosition(containerScreen, mouseX, mouseY, IEffectScreenExtension.CallReason.RECIPE_KEY);
            if (v != null) {
                builder.createBuilder(EFFECT_INGREDIENT_TYPE, v)
                        .buildWithArea((int) mouseX, (int) mouseY, 1, 1);
            }

            return Optional.empty();
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (Jeed.EMI || Jeed.REI) return;

        registration.addRecipeCatalyst(Items.POTION.getDefaultInstance(), EffectInfoRecipe.TYPE);
    }

    @Override
    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        if (Jeed.EMI || Jeed.REI) return;

        var focus = JEIPlugin.JEI_HELPERS.getFocusFactory().createFocus(RecipeIngredientRole.INPUT, JEIPlugin.EFFECT_INGREDIENT_TYPE, effect);

        IRecipesGui recipesGui = JEIPlugin.JEI_RUNTIME.getRecipesGui();
        recipesGui.show(focus);
    }


    //TODO: register keyword

}
