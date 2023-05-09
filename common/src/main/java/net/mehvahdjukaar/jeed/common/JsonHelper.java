package net.mehvahdjukaar.jeed.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    public static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    public static List<Potion> readPotionList(JsonArray resultArray) {
        List<Potion> results = new ArrayList<>();

        for (JsonElement result : resultArray) {
            String id = GsonHelper.getAsString(result.getAsJsonObject(), "id");
            results.add(getPotion(new ResourceLocation(id)));
        }

        return results;
    }

    public static Potion getPotion(ResourceLocation potionName) {
        var potion = BuiltInRegistries.POTION.getOptional(potionName);

        if (potion.isEmpty()) throw new JsonSyntaxException("Unknown potion '" + potionName + "'");
        return potion.get();
    }

    public static MobEffect getEffect(ResourceLocation effectName) {
        var effect = BuiltInRegistries.MOB_EFFECT.getOptional(effectName);

        if (effect.isEmpty()) throw new JsonSyntaxException("Unknown effect '" + effectName + "'");
        return effect.get();
    }
}
