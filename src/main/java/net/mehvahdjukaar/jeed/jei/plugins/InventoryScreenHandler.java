package net.mehvahdjukaar.jeed.jei.plugins;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.Focus;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import net.mehvahdjukaar.jeed.jei.JEIPlugin;
import net.mehvahdjukaar.jeed.mixins.DisplayEffectScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

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
            }
        }
        return null;
    }

    public static void onClickedEffect(MobEffectInstance effect, double x, double y, int button){

        Rect2i slotArea = new Rect2i((int)x, (int)y, 16, 16);
        IClickedIngredient<?> clicked = ClickedIngredient.create(effect, slotArea);

        JEIPlugin.JEI_RUNTIME.getRecipesGui().show(new Focus<Object>(IFocus.Mode.OUTPUT, clicked.getValue()));
    }

}