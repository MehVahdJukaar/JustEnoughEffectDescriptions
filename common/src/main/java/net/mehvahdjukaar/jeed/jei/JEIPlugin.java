package net.mehvahdjukaar.jeed.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.jei.display.EffectRecipeCategory;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceHelper;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.VanillaPlugin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

/**
 * Author: MehVahdJukaar
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = Jeed.res("jei_plugin");

    public static final IIngredientType<MobEffectInstance> EFFECT_INGREDIENT_TYPE = () -> MobEffectInstance.class;

    public static IJeiRuntime JEI_RUNTIME;
    public static IJeiHelpers JEI_HELPERS;
    public static IIngredientVisibility JEI_INGREDIENT_VISIBILITY;

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
        registration.register(EFFECT_INGREDIENT_TYPE, getEffectList(), new EffectInstanceHelper(), EffectInstanceRenderer.INSTANCE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        JEI_INGREDIENT_VISIBILITY = registry.getIngredientVisibility();
        JEI_HELPERS = registry.getJeiHelpers();

        for (MobEffectInstance e : getEffectList()) {

            ResourceLocation name = Registry.MOB_EFFECT.getKey(e.getEffect());

            List<EffectInfoRecipe> recipes = EffectInfoRecipe.create(new MobEffectInstance(e), "effect." + name.getNamespace() + "." +
                    name.getPath() + ".description");
            registry.addRecipes(EffectInfoRecipe.TYPE, recipes);
        }
    }

    private static List<MobEffectInstance> getEffectList() {
        return Registry.MOB_EFFECT.stream()
                .filter(e -> !Jeed.getHiddenEffects().contains(Registry.MOB_EFFECT.getKey(e).toString()))
                .map(MobEffectInstance::new)
                .filter(MobEffectInstance::showIcon)
                .filter(MobEffectInstance::isVisible)
                .toList();
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

    //TODO: register keyword

}