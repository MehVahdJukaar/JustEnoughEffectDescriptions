package net.mehvahdjukaar.jeed.jei.plugins;

import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.mehvahdjukaar.jeed.jei.EffectRecipeCategory;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class VanillaPlugin {

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.BEACON), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.POTION), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.SPLASH_POTION), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.LINGERING_POTION), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.TIPPED_ARROW), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.SUSPICIOUS_STEW), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.GOLDEN_APPLE), EffectRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), EffectRecipeCategory.UID);
    }


    public static void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(DisplayEffectsScreen.class, new InventoryScreenHandler<>());
    }
}
