package net.mehvahdjukaar.jeed.recipes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//items that can accept any potion
public class PotionProviderRecipe implements IRecipe<RecipeWrapper> {
    public static final IRecipeType<PotionProviderRecipe> TYPE = IRecipeType.register("jeed:potion_provider");
    public static final Serializer SERIALIZER = new Serializer();
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
    public boolean matches(RecipeWrapper inv, World worldIn) {
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
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return TYPE;
    }

    public NonNullList<ItemStack> getProviders() {
        return providers;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PotionProviderRecipe> {
        public Serializer() {
            this.setRegistryName(Jeed.res("potion_provider"));
        }

        @Override
        public PotionProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<ItemStack> providers = JsonHelper.readItemStackList(JSONUtils.getAsJsonArray(json, "providers"));

            List<Potion> potions;
            try {
                potions = JsonHelper.readPotionList(JSONUtils.getAsJsonArray(json, "potions"));
            }catch (Exception ignored){
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
        public PotionProviderRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            NonNullList<ItemStack> providers = NonNullList.withSize(i, ItemStack.EMPTY);

            for(int j = 0; j < providers.size(); ++j) {
                providers.set(j, buffer.readItem());
            }

            int x = buffer.readVarInt();
            List<Potion> potions = new ArrayList<>();

            for(int y = 0; y < x; ++y) {
                potions.add(JsonHelper.getPotion(buffer.readResourceLocation()));
            }

            return new PotionProviderRecipe(recipeId, providers, potions);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, PotionProviderRecipe recipe) {
            buffer.writeVarInt(recipe.providers.size());

            for (ItemStack result : recipe.providers) {
                buffer.writeItem(result);
            }

            buffer.writeVarInt(recipe.potions.size());

            for (Potion potion : recipe.potions) {
                buffer.writeResourceLocation(potion.getRegistryName());
            }
        }
    }
}
