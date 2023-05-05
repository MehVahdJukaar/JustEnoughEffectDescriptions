package net.mehvahdjukaar.jeed.plugin.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Optional;

public class InventoryScreenHandler<C extends AbstractContainerMenu, T extends EffectRenderingInventoryScreen<C>> implements IGuiContainerHandler<T>{

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
       return Optional.empty();
        // return Optional.ofNullable(  IInventoryScreenExtension.INSTANCE.getHoveredEffect(containerScreen, mouseX, mouseY,false));
    }


}