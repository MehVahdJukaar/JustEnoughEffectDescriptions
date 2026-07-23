// EMI has no 26.1 build yet - its newest release still targets 1.21.1, so there is no API to compile against.
// Kept here, commented out, so the plugin can be revived as soon as EMI updates. Still written against the 1.21 APIs.
//package net.mehvahdjukaar.jeed.plugin.emi;
//
//import dev.emi.emi.api.*;
//import dev.emi.emi.api.stack.EmiRegistryAdapter;
//import dev.emi.emi.api.stack.EmiStackInteraction;
//import dev.emi.emi.config.EffectLocation;
//import dev.emi.emi.config.EmiConfig;
//import net.mehvahdjukaar.jeed.Jeed;
//import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
//import net.mehvahdjukaar.jeed.common.IPlugin;
//import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
//import net.mehvahdjukaar.jeed.plugin.emi.display.EffectInfoRecipeCategory;
//import net.mehvahdjukaar.jeed.plugin.emi.display.EmiEffectInfoRecipe;
//import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectIngredientSerializer;
//import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectInstanceStack;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.core.Holder;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffect;
//import net.minecraft.world.effect.MobEffectInstance;
//
//@EmiEntrypoint
//public class EMIPlugin implements EmiPlugin, IPlugin {
//
//    public static final ResourceLocation EFFECTS_INFO_CATEGORY = Jeed.res("effects");
//    public static final EffectInfoRecipeCategory CATEGORY = new EffectInfoRecipeCategory(EFFECTS_INFO_CATEGORY);
//
//    public EMIPlugin() {
//        Jeed.PLUGIN = this;
//    }
//
//    @Override
//    public boolean rendersTooltips() {
//        //emi hijacks effect rendering. we cant cancel its tooltips. we omit ours. too bad. I wont mixin squared into its own mixins, dont ask for more than this
//        return EmiConfig.effectLocation != EffectLocation.TOP;
//    }
//
//    @Override
//    public void register(EmiRegistry registry) {
//        registry.addCategory(CATEGORY);
//        Jeed.getEffectList().stream().map(MobEffectInstance::new)
//                .map(EffectInstanceStack::new).forEach(registry::addEmiStack);
//        for (Holder<MobEffect> e : Jeed.getEffectList()) {
//            registry.addRecipe(EmiEffectInfoRecipe.create(e));
//        }
//
//        for (var e : ScreenExtensionsHandler.EXTENSIONS.entrySet()) {
//            var screenClass = (Class<AbstractContainerScreen<?>>) e.getKey();
//            var effect = (IEffectScreenExtension<AbstractContainerScreen<?>>) e.getValue();
//
//            registry.addStackProvider(screenClass, new ScreenExtension<>(effect));
//        }
//        //purposefully not adding a workstation? i think
//    }
//
//    @Override
//    public void initialize(EmiInitRegistry registry) {
//        registry.addRegistryAdapter(EmiRegistryAdapter.simple(MobEffect.class,
//                BuiltInRegistries.MOB_EFFECT, (e, t, d) -> new EffectInstanceStack(e, d)));
//        registry.addIngredientSerializer(EffectInstanceStack.class, new EffectIngredientSerializer());
//    }
//
//    @Override
//    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
//        EmiApi.getRecipeManager().getRecipesByInput(new EffectInstanceStack(effect))
//                .stream().findFirst().ifPresent(EmiApi::displayRecipe);
//    }
//
//    public record ScreenExtension<T extends AbstractContainerScreen<?>>
//            (IEffectScreenExtension<T> ext) implements EmiStackProvider<T> {
//
//        @Override
//        public EmiStackInteraction getStackAt(T screen, int x, int y) {
//            var clicked = ext.getEffectAtPosition(screen, x, y, IEffectScreenExtension.CallReason.RECIPE_KEY);
//            if (clicked != null) {
//                return new EmiStackInteraction(new EffectInstanceStack(clicked), null, false);
//            }
//            return EmiStackInteraction.EMPTY;
//        }
//    }
//}
