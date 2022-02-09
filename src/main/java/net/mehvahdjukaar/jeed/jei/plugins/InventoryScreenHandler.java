package net.mehvahdjukaar.jeed.jei.plugins;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.jei.JEIPlugin;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class InventoryScreenHandler <C extends AbstractContainerMenu, T extends EffectRenderingInventoryScreen<C>> implements IGuiContainerHandler<T> {

    @Nullable
    @Override
    public Object getIngredientUnderMouse(T screen, double x, double y) {
        return getHoveredEffect(screen, x, y);
    }

    @Nullable
    public static MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double x, double y){
        int width = 120;


        int minY = screen.getGuiTop();

        int minX = screen.getGuiLeft() - 124;
        int maxX = minX + width;
        if (x > minX && x < maxX && y > minY) {

            /*
            if(screen instanceof DisplayEffectScreenAccessor accessor && accessor.hasEffects()) {

                Collection<MobEffectInstance> collection = Minecraft.getInstance().player.getActiveEffects();
                List<MobEffectInstance> list = collection.stream().filter(ForgeHooksClient::shouldRender).sorted().collect(java.util.stream.Collectors.toList());
                if (!collection.isEmpty()) {

                    int dx = 33;
                    //vanilla bug here :/ should use other list size instead
                    if (collection.size() > 5) {
                        dx = 132 / (collection.size() - 1);
                    }
                    double rel = y - minY;
                    for (int e = 1; e <= list.size(); e++) {
                        if (rel < dx * e) {
                            return list.get(e-1);
                        }
                    }
                }
            }*/
        }
        return null;
    }

    public static void onClickedEffect(MobEffectInstance effect, double x, double y, int button){
        IJeiRuntime jeiRuntime = JEIPlugin.JEI_RUNTIME;
        IFocus<MobEffectInstance> focus = jeiRuntime.createFocus(RecipeIngredientRole.OUTPUT, JEIPlugin.EFFECT, effect);

        IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
        recipesGui.show(focus);
    }

}