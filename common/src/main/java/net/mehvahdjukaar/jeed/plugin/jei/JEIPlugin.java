package net.mehvahdjukaar.jeed.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectRecipeCategory;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceHelper;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.plugin.jei.plugins.VanillaPlugin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin, IPlugin {

    private static final ResourceLocation ID = Jeed.res("jei_plugin");

    public static final IIngredientType<MobEffectInstance> EFFECT_INGREDIENT_TYPE = () -> MobEffectInstance.class;

    public static IJeiRuntime JEI_RUNTIME;
    public static IJeiHelpers JEI_HELPERS;
    public static IIngredientVisibility JEI_INGREDIENT_VISIBILITY;

    public JEIPlugin() {
        Jeed.PLUGIN = this;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new EffectRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(EFFECT_INGREDIENT_TYPE,
                Jeed.getEffectList().stream().map(MobEffectInstance::new).toList(),
                new EffectInstanceHelper(), EffectInstanceRenderer.INSTANCE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        JEI_INGREDIENT_VISIBILITY = registry.getIngredientVisibility();
        JEI_HELPERS = registry.getJeiHelpers();

        for (MobEffect e : Jeed.getEffectList()) {
            List<EffectInfoRecipe> recipes = EffectInfoRecipe.create(e);
            registry.addRecipes(EffectInfoRecipe.TYPE, recipes);
        }
    }



    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        //TODO:
        //if (!Jeed.REI) VanillaPlugin.registerGuiHandlers(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        VanillaPlugin.registerRecipeCatalysts(registration);
    }

    @Override
    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        var focus = JEIPlugin.JEI_HELPERS.getFocusFactory().createFocus(RecipeIngredientRole.INPUT, JEIPlugin.EFFECT_INGREDIENT_TYPE, effect);

        IRecipesGui recipesGui = JEIPlugin.JEI_RUNTIME.getRecipesGui();
        recipesGui.show(focus);
    }

    //TODO: register keyword

}