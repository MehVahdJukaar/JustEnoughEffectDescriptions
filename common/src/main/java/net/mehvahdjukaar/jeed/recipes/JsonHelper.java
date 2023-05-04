package net.mehvahdjukaar.jeed.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
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
            String id = GsonHelper.getAsString(result.getAsJsonObject(), "id");
            results.add(getPotion(new ResourceLocation(id)));
        }

        return results;
    }

    public static Potion getPotion(ResourceLocation potionName) {

        Potion potion = ForgeRegistries.POTIONS.getValue(potionName);

        if (potion == null) throw new JsonSyntaxException("Unknown potion '" + potionName + "'");
        return potion;
    }

    public static MobEffect getEffect(ResourceLocation effectName) {

        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectName);

        if (effect == null) throw new JsonSyntaxException("Unknown effect '" + effectName + "'");
        return effect;
    }
}
