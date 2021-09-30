package net.mehvahdjukaar.jeed.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    public static NonNullList<ItemStack> readItemStackList(JsonArray resultArray) {
        NonNullList<ItemStack> results = NonNullList.create();

        for (JsonElement result : resultArray) {
            results.add(CraftingHelper.getItemStack(result.getAsJsonObject(), true));
        }

        return results;
    }

    public static List<Potion> readPotionList(JsonArray resultArray) {
        List<Potion> results = new ArrayList<>();

        for (JsonElement result : resultArray) {
            String id = JSONUtils.getAsString(result.getAsJsonObject(), "id");
            results.add(getPotion(new ResourceLocation(id)));
        }

        return results;
    }

    public static Potion getPotion(ResourceLocation potionName) {

        Potion potion = ForgeRegistries.POTION_TYPES.getValue(potionName);

        if (potion == null) throw new JsonSyntaxException("Unknown potion '" + potionName + "'");
        return potion;
    }

    public static Effect getEffect(ResourceLocation effectName) {

        Effect effect = ForgeRegistries.POTIONS.getValue(effectName);

        if (effect == null) throw new JsonSyntaxException("Unknown effect '" + effectName + "'");
        return effect;
    }
}
