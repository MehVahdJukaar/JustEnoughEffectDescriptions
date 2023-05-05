package net.mehvahdjukaar.jeed.plugin.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IRecipesGui;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Optional;

public abstract class InventoryScreenHandler<C extends AbstractContainerMenu, T extends EffectRenderingInventoryScreen<C>> implements IGuiContainerHandler<T>{



    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
        return IGuiContainerHandler.super.getClickableIngredientUnderMouse(containerScreen, mouseX, mouseY);
    }
}