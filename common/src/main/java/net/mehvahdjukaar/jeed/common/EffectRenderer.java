package net.mehvahdjukaar.jeed.common;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class EffectRenderer {

    protected final Minecraft mc;
    protected final boolean offset;

    protected EffectRenderer(boolean offset) {
        this.mc = Minecraft.getInstance();
        this.offset = offset;
    }

    public void render(GuiGraphics graphics, MobEffectInstance effectInstance) {
        render(graphics, effectInstance, 0, 0, 16, 16);
    }

    public void render(GuiGraphics graphics, MobEffectInstance effectInstance, int x, int y, int width, int height) {
        var effect = effectInstance.getEffect();

        MobEffectTextureManager textures = mc.getMobEffectTextures();
        TextureAtlasSprite sprite = textures.get(effect);

        render(graphics, sprite, x, y, width, height);
    }

    public void render(GuiGraphics graphics, TextureAtlasSprite sprite, int x, int y, int width, int height) {
        int o = offset ? -1 : 0;
        graphics.blit( x + o, y + o, 0, width + 2, height + 2, sprite);
    }


    public static List<Component> getTooltipsWithDescription(MobEffectInstance effectInstance, TooltipFlag tooltipFlag,
                                                             boolean reactsToShift, boolean showDuration) {
        List<Component> tooltip = new ArrayList<>();
        if (effectInstance != null) {

            MobEffect effect = effectInstance.getEffect().value();

            String name = I18n.get(effect.getDescriptionId());
            int amp = effectInstance.getAmplifier();
            if (amp >= 1 && amp <= 9) {
                name = name + ' ' + I18n.get("enchantment.level." + (amp + 1));
            }

            tooltip.add(Component.literal(name));

            if (showDuration) {
                tooltip.add(MobEffectUtil.formatDuration(effectInstance, 1.0F,
                        Minecraft.getInstance().level.tickRateManager().tickrate()));
            }

            if (Jeed.hasEffectColor()) {
                MutableComponent colorValue = Component.literal("#" + Integer.toHexString(effect.getColor()));
                colorValue.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(effect.getColor())));

                MutableComponent color = Component.translatable("jeed.tooltip.color").withStyle(ChatFormatting.GRAY);

                tooltip.add(Component.translatable("jeed.tooltip.color_complete", color, colorValue));
            }

            var cat = effect.getCategory();
            if (effect.isBeneficial()) {
                tooltip.add(Component.translatable("jeed.tooltip.beneficial").withStyle(ChatFormatting.BLUE));
            }
            else if (cat == MobEffectCategory.NEUTRAL) {
                tooltip.add(Component.translatable("jeed.tooltip.neutral").withStyle(ChatFormatting.GRAY));
            }
            else {
                tooltip.add(Component.translatable("jeed.tooltip.harmful").withStyle(ChatFormatting.RED));
            }


            boolean showDescription = reactsToShift && Screen.hasShiftDown();
            //show full description with shift
            ResourceLocation res = null;
            if (showDescription || tooltipFlag.isAdvanced()) {
                res = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            }

            if (showDescription) {

                tooltip.add(Component.translatable("effect." + res.getNamespace() + "." +
                        res.getPath() + ".description").withStyle(ChatFormatting.GRAY));
            } else {

                List<Pair<Holder<Attribute>, AttributeModifier>> attributes = Lists.newArrayList();

                Holder<MobEffect> holder = effectInstance.getEffect();
                holder.value().createModifiers(effectInstance.getAmplifier(), (holderx, attributeModifierx) -> {
                    attributes.add(new Pair<>(holderx, attributeModifierx));
                });



                if (!attributes.isEmpty()) {

                    tooltip.add(Component.empty());
                    tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

                    for (var pair : attributes) {
                        AttributeModifier am = pair.getSecond();
                        double amount = am.amount();
                        double actualAmount;
                        if (am.operation() !=  AttributeModifier.Operation.ADD_MULTIPLIED_BASE && am.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                            actualAmount = am.amount();
                        } else {
                            actualAmount = am.amount() * 100.0D;
                        }

                        var descriptionId = pair.getFirst().value().getDescriptionId();
                        if (amount > 0.0D) {
                            tooltip.add((Component.translatable("attribute.modifier.plus." + am.operation().id(),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(actualAmount), Component.translatable(descriptionId))).withStyle(ChatFormatting.BLUE));
                        } else if (amount < 0.0D) {
                            actualAmount = actualAmount * -1.0D;
                            tooltip.add((Component.translatable("attribute.modifier.take." + am.operation().id(),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(actualAmount), Component.translatable(descriptionId))).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }

            if (tooltipFlag.isAdvanced()) {
                tooltip.add(Component.literal(res.toString()).withStyle(ChatFormatting.DARK_GRAY));
            }

        }
        return tooltip;
    }

}
