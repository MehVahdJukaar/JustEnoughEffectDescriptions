package net.mehvahdjukaar.jeed.jei.ingredient;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectInstanceRenderer implements IIngredientRenderer<EffectInstance> {

    public static final EffectInstanceRenderer INSTANCE = new EffectInstanceRenderer(true);

    public static final EffectInstanceRenderer INSTANCE_SLOT = new EffectInstanceRenderer(false);

    private final Minecraft MC;

    private final boolean offset;

    public EffectInstanceRenderer(boolean offset) {
        MC = Minecraft.getInstance();
        this.offset = offset;
    }

    @Override
    public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable EffectInstance effectInstance) {

        if (effectInstance != null ) {
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();

            PotionSpriteUploader potionspriteuploader = MC.getMobEffectTextures();

            Effect effect = effectInstance.getEffect();
            TextureAtlasSprite textureatlassprite = potionspriteuploader.get(effect);
            MC.getTextureManager().bind(textureatlassprite.atlas().location());
            int o = offset ? -1 : 0;
            AbstractGui.blit(matrixStack, xPosition + o, yPosition+ o, 0, 18, 18, textureatlassprite);


            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
    }


    @Override
    public List<ITextComponent> getTooltip(EffectInstance effectInstance, ITooltipFlag tooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();
        Effect effect = effectInstance.getEffect();
        if (effect != null) {



            String name = I18n.get(effectInstance.getEffect().getDescriptionId());
            int amp = effectInstance.getAmplifier();
            if (amp >= 1 && amp <= 9) {
                name = name + ' ' + I18n.get("enchantment.level." + (amp + 1));
            }

            //ITextComponent displayName = effectInstance.getEffect().getDisplayName();
            //tooltip.add(displayName);
            tooltip.add(new StringTextComponent(name));

            StringTextComponent colorValue = new StringTextComponent("#" + Integer.toHexString(effect.getColor()));
            colorValue.setStyle(Style.EMPTY.withColor(Color.fromRgb(effect.getColor())));

            IFormattableTextComponent color = new TranslationTextComponent("jeed.tooltip.color").withStyle(TextFormatting.GRAY);

            tooltip.add(new TranslationTextComponent("jeed.tooltip.color_complete", color, colorValue));
            if(effect.isBeneficial()){
                tooltip.add(new TranslationTextComponent("jeed.tooltip.beneficial").withStyle(TextFormatting.BLUE));
            }
            else{
                tooltip.add(new TranslationTextComponent("jeed.tooltip.harmful").withStyle(TextFormatting.RED));
            }

            List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
            Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
            if (!map.isEmpty()) {
                for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                    AttributeModifier attributemodifier = entry.getValue();
                    AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectInstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                    list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                }
            }
            if (!list1.isEmpty()) {
                tooltip.add(StringTextComponent.EMPTY);
                tooltip.add((new TranslationTextComponent("potion.whenDrank")).withStyle(TextFormatting.DARK_PURPLE));

                for(Pair<Attribute, AttributeModifier> pair : list1) {
                    AttributeModifier attributemodifier2 = pair.getSecond();
                    double d0 = attributemodifier2.getAmount();
                    double d1;
                    if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        d1 = attributemodifier2.getAmount();
                    } else {
                        d1 = attributemodifier2.getAmount() * 100.0D;
                    }

                    if (d0 > 0.0D) {
                        tooltip.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.BLUE));
                    } else if (d0 < 0.0D) {
                        d1 = d1 * -1.0D;
                        tooltip.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.RED));
                    }
                }
            }

            if(tooltipFlag.isAdvanced()){
                tooltip.add(new StringTextComponent(effect.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
            }

        }
        return tooltip;
    }

}
