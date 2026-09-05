// EMI has no 26.1 build yet - its newest release still targets 1.21.1, so there is no API to compile against.
// Kept here, commented out, so the plugin can be revived as soon as EMI updates. Still written against the 1.21 APIs.
//package net.mehvahdjukaar.jeed.plugin.emi.ingredient;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.mojang.serialization.JsonOps;
//import dev.emi.emi.api.stack.EmiIngredient;
//import dev.emi.emi.api.stack.EmiStack;
//import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
//import io.netty.handler.codec.DecoderException;
//import io.netty.handler.codec.EncoderException;
//import net.mehvahdjukaar.jeed.Jeed;
//import net.minecraft.world.effect.MobEffectInstance;
//
//public class EffectIngredientSerializer implements EmiIngredientSerializer<EffectInstanceStack> {
//    @Override
//    public String getType() {
//        return "mob_effect";
//    }
//
//    @Override
//    public EmiIngredient deserialize(JsonElement element) {
//        try {
//            return MobEffectInstance.CODEC.decode(JsonOps.INSTANCE, element).result().map(a -> new EffectInstanceStack(a.getFirst()))
//                    .orElseThrow(DecoderException::new);
//        } catch (DecoderException e) {
//            Jeed.LOGGER.error("Error parsing mob effect");
//            Jeed.LOGGER.error(e);
//            return EmiStack.EMPTY;
//        }
//    }
//
//    @Override
//    public JsonElement serialize(EffectInstanceStack stack) {
//        try {
//            return MobEffectInstance.CODEC.encodeStart(JsonOps.INSTANCE, stack.getEffect()).result().orElseThrow(EncoderException::new);
//        } catch (EncoderException e) {
//            Jeed.LOGGER.error("Error encoding mob effect");
//            Jeed.LOGGER.error(e);
//            return new JsonObject();
//        }
//    }
//
//}
