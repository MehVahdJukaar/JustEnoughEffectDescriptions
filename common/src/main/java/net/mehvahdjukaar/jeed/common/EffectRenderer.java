package net.mehvahdjukaar.jeed.common;

import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;

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
        graphics.blit(x + o, y + o, 0, width + 2, height + 2, sprite);
    }


    public static List<Component> getTooltipsWithDescription(MobEffectInstance effectInstance, TooltipFlag tooltipFlag, boolean reactsToShift, boolean showDuration) {
        List<Component> tooltip = new ArrayList<>();
        if (effectInstance != null) {

            MobEffect effect = effectInstance.getEffect().value();

            String name = I18n.get(effect.getDescriptionId());
            int amp = effectInstance.getAmplifier();
            if (amp >= 1 && amp <= 9) {
                name = name + ' ' + I18n.get("enchantment.level." + (amp + 1));
            }
            float tickRate = Minecraft.getInstance().level.tickRateManager().tickrate();

            tooltip.add(Component.literal(name));

            if (showDuration && !effectInstance.endsWithin(20)) {
                tooltip.add(MobEffectUtil.formatDuration(effectInstance, 1.0F, tickRate));
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
            } else if (cat == MobEffectCategory.NEUTRAL) {
                tooltip.add(Component.translatable("jeed.tooltip.neutral").withStyle(ChatFormatting.GRAY));
            } else {
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
                //copied from PotionContent.addPotionTooltip
                List<Component> newToolTips = new ArrayList<>();
                PotionContents.addPotionTooltip(List.of(effectInstance), newToolTips::add, 1, tickRate);
                if (!newToolTips.isEmpty()) {
                    //remove name
                  //  newToolTips.removeFirst();
                }
                tooltip.addAll(newToolTips);
            }

            if (tooltipFlag.isAdvanced()) {
                tooltip.add(Component.literal(res.toString()).withStyle(ChatFormatting.DARK_GRAY));
            }

        }
        return tooltip;
    }

}
