package net.mehvahdjukaar.jeed.recipes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.jeed.JeedPlatform;
import net.mehvahdjukaar.jeed.common.JsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//items that can accept any potion
public class PotionProviderRecipe implements Recipe<CraftingContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> providers;
    //empty potions list means it applies to all of them
    private final List<Potion> potions;

    public PotionProviderRecipe(ResourceLocation id, NonNullList<Ingredient> providers, List<Potion> potions) {
        this.id = id;
        this.providers = providers;
        this.potions = potions;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return "potion_provider";
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return providers;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer wrapper) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JeedPlatform.getPotionProviderSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return JeedPlatform.getPotionProviderType();
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public static class Serializer implements RecipeSerializer<PotionProviderRecipe> {

        @Override
        public PotionProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<Ingredient> providers = JsonHelper.readIngredients(GsonHelper.getAsJsonArray(json, "providers"));

            List<Potion> potions = JsonHelper.readPotionList(GsonHelper.getAsJsonArray(json, "potions"));

            if (providers.isEmpty()) {
                throw new JsonParseException("No effect providers for recipe");
            } else {
                return new PotionProviderRecipe(recipeId, providers, potions);
            }
        }

        @Override
        @Nullable
        public PotionProviderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> providers = NonNullList.withSize(i, Ingredient.EMPTY);

            providers.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            int x = buffer.readVarInt();
            List<Potion> potions = new ArrayList<>();

            for (int y = 0; y < x; ++y) {
                potions.add(JsonHelper.getPotion(buffer.readResourceLocation()));
            }

            return new PotionProviderRecipe(recipeId, providers, potions);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PotionProviderRecipe recipe) {
            buffer.writeVarInt(recipe.providers.size());

            for (Ingredient result : recipe.providers) {
                result.toNetwork(buffer);
            }

            buffer.writeVarInt(recipe.potions.size());

            for (Potion potion : recipe.potions) {
                buffer.writeResourceLocation(Registry.POTION.getKey(potion));
            }
        }
    }
}
