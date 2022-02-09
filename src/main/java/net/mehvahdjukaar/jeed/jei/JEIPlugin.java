package net.mehvahdjukaar.jeed.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceHelper;
import net.mehvahdjukaar.jeed.jei.ingredient.EffectInstanceRenderer;
import net.mehvahdjukaar.jeed.jei.plugins.VanillaPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = Jeed.res("jei_plugin");

    public static final IIngredientType<MobEffectInstance> EFFECT = () -> MobEffectInstance.class;

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
        registration.register(EFFECT, getEffectList(), new EffectInstanceHelper(), EffectInstanceRenderer.INSTANCE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        JEI_INGREDIENT_VISIBILITY = registry.getIngredientVisibility();

        for (MobEffectInstance e : getEffectList()) {

            ResourceLocation name = e.getEffect().getRegistryName();

            registerEffectInfo(registry, new MobEffectInstance(e), EFFECT, "effect." + name.getNamespace() + "." +
                    name.getPath() + ".description");
        }
    }

    private static List<MobEffectInstance> getEffectList() {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(e -> !Jeed.HIDDEN_EFFECTS.get().contains(e.getRegistryName().toString()))
                .map(MobEffectInstance::new)
                .filter(MobEffectInstance::showIcon)
                .filter(MobEffectInstance::isVisible)
                .collect(Collectors.toList());
    }

    public void registerEffectInfo(IRecipeRegistration registration, MobEffectInstance ingredient, IIngredientType<MobEffectInstance> ingredientType, String descriptionKey) {

        List<EffectInfoRecipe> recipes = EffectInfoRecipe.create(ingredient, ingredientType, descriptionKey);
        registration.addRecipes(recipes, EffectRecipeCategory.UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (!Jeed.REI) VanillaPlugin.registerGuiHandlers(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        VanillaPlugin.registerRecipeCatalysts(registration);
    }

    //TODO: register keyword

    public static IJeiRuntime JEI_RUNTIME;
    public static IIngredientVisibility JEI_INGREDIENT_VISIBILITY;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;
    }

    //ISubtypeRegistration

}