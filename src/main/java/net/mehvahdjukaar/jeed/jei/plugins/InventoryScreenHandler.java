package net.mehvahdjukaar.jeed.jei.plugins;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.jei.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryScreenHandler<C extends AbstractContainerMenu, T extends EffectRenderingInventoryScreen<C>> implements IGuiContainerHandler<T> {

    @Nullable
    @Override
    public Object getIngredientUnderMouse(T screen, double x, double y) {
        return getHoveredEffect(screen, x, y, false);
    }

    //TODO: re add this for to work with effects on left & right
    @Nullable
    public static MobEffectInstance getHoveredEffect(AbstractContainerScreen<?> screen, double mouseX, double mouseY, boolean ignoreIfSmall) {
        int minX;
        boolean cancelShift = false;
        if (cancelShift)
            minX = (screen.width - screen.getXSize()) / 2;
        else
            minX = screen.getGuiLeft() + screen.getXSize() + 2;
        int x = screen.width - minX;
        Collection<MobEffectInstance> collection = Minecraft.getInstance().player.getActiveEffects();
        if (!collection.isEmpty() && x >= 32) {

            boolean full = x >= 120;
            var event = ForgeHooksClient.onScreenPotionSize(screen);
            if (event != Event.Result.DEFAULT) {
                full = event == Event.Result.DENY; // true means classic mode
            }

            if (!full && ignoreIfSmall) return null;
            int width = full ? 120 : 32;
            if (mouseX > minX && mouseX < minX + width) {

                int spacing = 33;
                if (collection.size() > 5) {
                    spacing = 132 / (collection.size() - 1);
                }


                List<MobEffectInstance> iterable = collection.stream().filter(ForgeHooksClient::shouldRenderEffect).sorted().collect(Collectors.toList());

                int minY = screen.getGuiTop();
                int maxHeight = iterable.size() * spacing;

                if (mouseY > minY && mouseY < minY + maxHeight) {
                    return iterable.get((int) ((mouseY - minY) / spacing));
                }
            }
        }
        return null;
    }

    public static void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        var focus = JEIPlugin.JEI_HELPERS.getFocusFactory().createFocus(RecipeIngredientRole.INPUT, JEIPlugin.EFFECT, effect);

        IRecipesGui recipesGui = JEIPlugin.JEI_RUNTIME.getRecipesGui();
        recipesGui.show(focus);
    }

}