package net.mehvahdjukaar.jeed.plugin.emi.ingredient;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.EmiConfig;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Collections;
import java.util.List;

public class EffectInstanceStack extends EmiStack {
    private static final EffectRenderer RENDERER = new EffectRenderer(false) {
    };

    private final MobEffectInstance effect;

    public EffectInstanceStack(MobEffectInstance effect) {
        this.effect = effect;
    }

    public EffectInstanceStack(MobEffect effect, long duration) {
        this(new MobEffectInstance(effect, (int) duration));
    }

    public EffectInstanceStack(MobEffect effect) {
        this(effect, 30 * 20);
    }


    public MobEffectInstance getEffect() {
        return effect;
    }

    @Override
    public EmiStack copy() {
        return new EffectInstanceStack(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(),
                effect.isAmbient(), effect.isVisible(), effect.showIcon(), effect.hiddenEffect, effect.getFactorData()));
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        RENDERER.render(draw, effect, x - 1, y - 1, 16, 16);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return effect.getEffect();
    }

    @Override
    public ResourceLocation getId() {
        return BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect());
    }

    @Override
    public List<Component> getTooltipText() {
        Minecraft client = Minecraft.getInstance();
        return EffectRenderer.getTooltipsWithDescription(effect,
                client.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL,
                false, false);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> list = Lists.newArrayList();
        list.addAll(getTooltipText().stream().map(EmiPort::ordered).map(ClientTooltipComponent::create).toList());
        ResourceLocation id = this.getId();
        if (EmiConfig.appendModId && id != null) {
            String mod = EmiUtil.getModName(id.getNamespace());
            list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(mod, ChatFormatting.BLUE, ChatFormatting.ITALIC))));
        }

        list.addAll(super.getTooltip());
        return list;
    }

    @Override
    public Component getName() {
        return effect.getEffect().getDisplayName();
    }

    @Override
    public ItemStack getItemStack() {
        var item = PotionUtils.setCustomEffects(new ItemStack(Items.POTION), Collections.singletonList(normalize()));
        item.getOrCreateTag().putInt("CustomPotionColor", effect.getEffect().getColor());
        return item;
    }

    public MobEffectInstance normalize() {
        return new MobEffectInstance(effect.getEffect(), 30 * 20, 0, effect.isAmbient(),
                effect.isVisible(), effect.showIcon(), effect.hiddenEffect, effect.getFactorData());
    }
}
