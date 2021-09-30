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
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class EffectProviderRecipe implements IRecipe<RecipeWrapper> {
    public static final IRecipeType<EffectProviderRecipe> TYPE = IRecipeType.register("jeed:effect_provider");
    public static final Serializer SERIALIZER = new Serializer();
    private final ResourceLocation id;
    private final Effect effect;
    private final NonNullList<ItemStack> providers;

    public EffectProviderRecipe(ResourceLocation id, Effect effect,  NonNullList<ItemStack> providers) {
        this.id = id;
        this.effect = effect;
        this.providers = providers;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return "effect_provider";
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
        return null;
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

    public Effect getEffect() {
        return effect;
    }

    public NonNullList<ItemStack> getProviders() {
        return providers;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EffectProviderRecipe> {
        public Serializer() {
            this.setRegistryName(Jeed.res("effect_provider"));
        }

        @Override
        public EffectProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<ItemStack> providers = JsonHelper.readItemStackList(JSONUtils.getAsJsonArray(json, "providers"));

            String effectID = JSONUtils.getAsString(json.getAsJsonObject("effect"), "id");
            Effect effect = JsonHelper.getEffect(new ResourceLocation(effectID));
            if (providers.isEmpty()) {
                throw new JsonParseException("No effect providers for recipe");
            } else {
                return new EffectProviderRecipe(recipeId, effect, providers);
            }
        }

        @Override
        @Nullable
        public EffectProviderRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            Effect effect = JsonHelper.getEffect(id);
            int i = buffer.readVarInt();
            NonNullList<ItemStack> providers = NonNullList.withSize(i, ItemStack.EMPTY);

            for(int j = 0; j < providers.size(); ++j) {
                providers.set(j, buffer.readItem());
            }
            return new EffectProviderRecipe(recipeId, effect, providers);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, EffectProviderRecipe recipe) {
            buffer.writeResourceLocation(recipe.effect.getRegistryName());
            buffer.writeVarInt(recipe.providers.size());

            for (ItemStack result : recipe.providers) {
                buffer.writeItem(result);
            }
        }


    }
}
