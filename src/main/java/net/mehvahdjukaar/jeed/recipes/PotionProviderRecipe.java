package net.mehvahdjukaar.jeed.recipes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//items that can accept any potion
public class PotionProviderRecipe implements Recipe<RecipeWrapper> {

    private final ResourceLocation id;
    private final NonNullList<ItemStack> providers;
    //empty potions list means it applies to all of them
    private final List<Potion> potions;

    public PotionProviderRecipe(ResourceLocation id, NonNullList<ItemStack> providers, List<Potion> potions) {
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
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(Ingredient.EMPTY);
        return nonNullList;
    }

    //let's hope this won't cause troubles
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper wrapper) {
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
        return Jeed.POTION_PROVIDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Jeed.POTION_PROVIDER_TYPE.get();
    }

    public NonNullList<ItemStack> getProviders() {
        return providers;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public static class Serializer implements RecipeSerializer<PotionProviderRecipe> {

        @Override
        public PotionProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<ItemStack> providers = JsonHelper.readItemStackList(GsonHelper.getAsJsonArray(json, "providers"));

            List<Potion> potions;
            try {
                potions = JsonHelper.readPotionList(GsonHelper.getAsJsonArray(json, "potions"));
            } catch (Exception ignored) {
                potions = new ArrayList<>();
            }

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
            NonNullList<ItemStack> providers = NonNullList.withSize(i, ItemStack.EMPTY);

            for (int j = 0; j < providers.size(); ++j) {
                providers.set(j, buffer.readItem());
            }

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

            for (ItemStack result : recipe.providers) {
                buffer.writeItem(result);
            }

            buffer.writeVarInt(recipe.potions.size());

            for (Potion potion : recipe.potions) {
                buffer.writeResourceLocation(ForgeRegistries.POTIONS.getKey(potion));
            }
        }
    }
}
