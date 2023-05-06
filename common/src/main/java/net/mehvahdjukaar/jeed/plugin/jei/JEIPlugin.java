package net.mehvahdjukaar.jeed.plugin.jei;

import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.EffectCategory;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.jei.display.EffectRecipeCategory;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceHelper;
import net.mehvahdjukaar.jeed.plugin.jei.ingredient.EffectInstanceRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        for (var e : ScreenExtensionsHandler.EXTENSIONS.entrySet()) {
            Class<? extends AbstractContainerScreen<?>> screenClass = (Class<? extends AbstractContainerScreen<?>>) e.getKey();
            IEffectScreenExtension<AbstractContainerScreen<?>> effect = (IEffectScreenExtension<AbstractContainerScreen<?>>) e.getValue();
            ScreenExtension<AbstractContainerScreen<?>> extension = new ScreenExtension<>(effect);

            registration.addGuiContainerHandler(screenClass, extension);
        }
    }

    public static class ScreenExtension<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<T> {

        private final IEffectScreenExtension<T> ext;

        public ScreenExtension(IEffectScreenExtension<T> ext) {
            this.ext = ext;
        }

        @Override
        public @Nullable Object getIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
            return ext.getEffectAtPosition(containerScreen, mouseX, mouseY, IEffectScreenExtension.CallReason.RECIPE_KEY);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Items.POTION.getDefaultInstance(), EffectInfoRecipe.TYPE);
    }

    @Override
    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        var focus = JEIPlugin.JEI_HELPERS.getFocusFactory().createFocus(RecipeIngredientRole.INPUT, JEIPlugin.EFFECT_INGREDIENT_TYPE, effect);

        IRecipesGui recipesGui = JEIPlugin.JEI_RUNTIME.getRecipesGui();
        recipesGui.show(focus);
    }

    @Override
    public int getMaxTextWidth() {
        return EffectCategory.RECIPE_WIDTH;
    }


    //TODO: register keyword

}