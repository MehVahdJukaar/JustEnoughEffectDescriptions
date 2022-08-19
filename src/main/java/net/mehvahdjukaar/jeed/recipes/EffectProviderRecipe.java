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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public class EffectProviderRecipe implements Recipe<RecipeWrapper> {

    private final ResourceLocation id;
    private final MobEffect effect;
    private final boolean allEffects;
    private final NonNullList<ItemStack> providers;

    public EffectProviderRecipe(ResourceLocation id, MobEffect effect, NonNullList<ItemStack> providers) {
        this.id = id;
        this.effect = effect;
        this.providers = providers;
        this.allEffects = false;
    }

    public EffectProviderRecipe(ResourceLocation id, NonNullList<ItemStack> providers) {
        this.id = id;
        this.effect = null;
        this.providers = providers;
        this.allEffects = true;
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
        return Jeed.EFFECT_PROVIDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Jeed.EFFECT_PROVIDER_TYPE.get();
    }

    public Collection<MobEffect> getEffects() {
        return allEffects ? ForgeRegistries.MOB_EFFECTS.getValues() : Collections.singletonList(effect);
    }


    public NonNullList<ItemStack> getProviders() {
        return providers;
    }

    public static class Serializer implements RecipeSerializer<EffectProviderRecipe> {

        @Override
        public EffectProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<ItemStack> providers = JsonHelper.readItemStackList(GsonHelper.getAsJsonArray(json, "providers"));

            if (providers.isEmpty()) {
                throw new JsonParseException("No effect providers for recipe");
            } else {
                String effectID = GsonHelper.getAsString(json.getAsJsonObject("effect"), "id");
                if (effectID.equals("all") || effectID.equals("minecraft:all")) {
                    return new EffectProviderRecipe(recipeId, providers);
                } else {
                    MobEffect effect = JsonHelper.getEffect(new ResourceLocation(effectID));
                    return new EffectProviderRecipe(recipeId, effect, providers);
                }
            }
        }

        @Override
        @Nullable
        public EffectProviderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<ItemStack> providers = NonNullList.withSize(i, ItemStack.EMPTY);

            for (int j = 0; j < providers.size(); ++j) {
                providers.set(j, buffer.readItem());
            }
            ResourceLocation id = buffer.readResourceLocation();

            if (id.getPath().equals("all")) {
                return new EffectProviderRecipe(recipeId, providers);
            } else {
                MobEffect effect = JsonHelper.getEffect(id);
                return new EffectProviderRecipe(recipeId, effect, providers);
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EffectProviderRecipe recipe) {
            buffer.writeVarInt(recipe.providers.size());

            for (ItemStack result : recipe.providers) {
                buffer.writeItem(result);
            }

            ResourceLocation res;
            if (recipe.allEffects) {
                res = new ResourceLocation("all");
            } else {
                res = ForgeRegistries.MOB_EFFECTS.getKey(recipe.effect);
            }
            buffer.writeResourceLocation(res);

        }


    }
}
