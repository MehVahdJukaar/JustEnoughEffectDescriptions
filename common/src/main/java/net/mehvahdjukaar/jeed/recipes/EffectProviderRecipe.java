package net.mehvahdjukaar.jeed.recipes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.JsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public class EffectProviderRecipe implements Recipe<CraftingContainer> {

    private final ResourceLocation id;
    @Nullable
    private final MobEffect effect;
    private final NonNullList<Ingredient> providers;

    public EffectProviderRecipe(ResourceLocation id, @Nullable MobEffect effect, NonNullList<Ingredient> providers) {
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
        return providers;
    }

    //let's hope this won't cause troubles
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
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
        return Jeed.getEffectProviderSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return Jeed.getEffectProviderType();
    }

    public Collection<MobEffect> getEffects() {
        return effect == null ? BuiltInRegistries.MOB_EFFECT.stream().toList() : Collections.singletonList(effect);
    }


    public static class Serializer implements RecipeSerializer<EffectProviderRecipe> {

        @Override
        public EffectProviderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            NonNullList<Ingredient> providers = JsonHelper.readIngredients(GsonHelper.getAsJsonArray(json, "providers"));

            if (providers.isEmpty()) {
                throw new JsonParseException("No effect providers for recipe");
            } else {
                var v = json.get("effect");
                if (v == null) {
                    throw new JsonParseException("Missing effect for recipe");
                }
                String effectID;
                if (v instanceof JsonObject jo) {
                    effectID = GsonHelper.getAsString(jo, "id");
                } else effectID = v.getAsString();

                MobEffect effect = null;
                if (effectID != null && !effectID.equals("all") && !effectID.equals("minecraft:all")) {
                    effect = JsonHelper.getEffect(new ResourceLocation(effectID));
                }
                return new EffectProviderRecipe(recipeId, effect, providers);
            }
        }

        @Override
        @Nullable
        public EffectProviderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> providers = NonNullList.withSize(i, Ingredient.EMPTY);

            providers.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            ResourceLocation id = buffer.readResourceLocation();

            MobEffect effect = null;
            if (!id.getPath().equals("all")) {
                effect = JsonHelper.getEffect(id);
            }
            return new EffectProviderRecipe(recipeId, effect, providers);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EffectProviderRecipe recipe) {
            buffer.writeVarInt(recipe.providers.size());

            for (Ingredient result : recipe.providers) {
                result.toNetwork(buffer);
            }

            ResourceLocation res;
            if (recipe.effect == null) {
                res = new ResourceLocation("all");
            } else {
                res = BuiltInRegistries.MOB_EFFECT.getKey(recipe.effect);
            }
            buffer.writeResourceLocation(res);
        }
    }
}
