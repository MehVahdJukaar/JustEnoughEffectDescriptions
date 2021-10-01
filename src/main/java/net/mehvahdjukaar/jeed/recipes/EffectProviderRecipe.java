package net.mehvahdjukaar.jeed.recipes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class EffectProviderRecipe implements Recipe<RecipeWrapper> {
    public static final RecipeType<EffectProviderRecipe> TYPE = RecipeType.register("jeed:effect_provider");
    public static final Serializer SERIALIZER = new Serializer();
    private final ResourceLocation id;
    private final MobEffect effect;
    private final NonNullList<ItemStack> providers;

    public EffectProviderRecipe(ResourceLocation id, MobEffect effect,  NonNullList<ItemStack> providers) {
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
    public boolean matches(RecipeWrapper inv, Level worldIn) {
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
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public MobEffect getEffect() {
        return effect;
    }

    public NonNullList<ItemStack> getProviders() {
        return providers;
    }

    private static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EffectProviderRecipe> {
        public Serializer() {
            this.setRegistryName(Jeed.res("effect_provider"));
        }

        @Override
        public EffectProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<ItemStack> providers = JsonHelper.readItemStackList(GsonHelper.getAsJsonArray(json, "providers"));

            String effectID = GsonHelper.getAsString(json.getAsJsonObject("effect"), "id");
            MobEffect effect = JsonHelper.getEffect(new ResourceLocation(effectID));
            if (providers.isEmpty()) {
                throw new JsonParseException("No effect providers for recipe");
            } else {
                return new EffectProviderRecipe(recipeId, effect, providers);
            }
        }

        @Override
        @Nullable
        public EffectProviderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            MobEffect effect = JsonHelper.getEffect(id);
            int i = buffer.readVarInt();
            NonNullList<ItemStack> providers = NonNullList.withSize(i, ItemStack.EMPTY);

            for(int j = 0; j < providers.size(); ++j) {
                providers.set(j, buffer.readItem());
            }
            return new EffectProviderRecipe(recipeId, effect, providers);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EffectProviderRecipe recipe) {
            buffer.writeResourceLocation(recipe.effect.getRegistryName());
            buffer.writeVarInt(recipe.providers.size());

            for (ItemStack result : recipe.providers) {
                buffer.writeItem(result);
            }
        }


    }
}
