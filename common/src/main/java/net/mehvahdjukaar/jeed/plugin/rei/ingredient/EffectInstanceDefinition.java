package net.mehvahdjukaar.jeed.plugin.rei.ingredient;//


import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.stream.Stream;

public class EffectInstanceDefinition implements EntryDefinition<MobEffectInstance>, EntrySerializer<MobEffectInstance> {

    @Override
    public void fillCrashReport(CrashReport report, CrashReportCategory category, EntryStack<MobEffectInstance> entry) {
        EntryDefinition.super.fillCrashReport(report, category, entry);
        MobEffect effect = entry.getValue().getEffect();
        if (effect != null) {
            Component displayName = entry.getValue().getEffect().getDisplayName();
            category.setDetail("Effect", displayName::getString);
        } else {
            category.setDetail("Effect", () -> "null");
        }
        category.setDetail("Duration", () -> String.valueOf(entry.getValue().getDuration()));
        category.setDetail("Amplifier", () -> String.valueOf(entry.getValue().getAmplifier()));
    }

    @Override
    public Class<MobEffectInstance> getValueType() {
        return MobEffectInstance.class;
    }

    @Override
    public EntryType<MobEffectInstance> getType() {
        return REIPlugin.EFFECT_ENTRY_TYPE;
    }

    @Override
    public EntryRenderer<MobEffectInstance> getRenderer() {
        return EffectInstanceRenderer.INSTANCE;
    }

    @Override
    public @Nullable ResourceLocation getIdentifier(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return BuiltInRegistries.MOB_EFFECT.getKey(value.getEffect());
    }

    @Override
    public boolean isEmpty(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return false;
    }

    @Override
    public MobEffectInstance copy(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), value.getDuration(), value.getAmplifier(), value.isAmbient(),
                value.isVisible(), value.showIcon(), value.hiddenEffect, value.getFactorData());
    }

    @Override
    public MobEffectInstance normalize(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), 30 * 20, 0, value.isAmbient(),
                value.isVisible(), value.showIcon(), value.hiddenEffect, value.getFactorData());
    }

    @Override
    public MobEffectInstance wildcard(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), 30 * 20);
    }

    @Override
    public long hash(EntryStack<MobEffectInstance> entry, MobEffectInstance value, ComparisonContext context) {
        return value.hashCode();
    }

    @Override
    public boolean equals(MobEffectInstance o1, MobEffectInstance o2, ComparisonContext context) {
        return o2.getEffect() == o1.getEffect();
    }

    @Override
    public @Nullable ItemStack cheatsAs(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return PotionUtils.setCustomEffects(new ItemStack(Items.POTION),
                Collections.singletonList(normalize(entry, value)));
    }

    @Override
    public @Nullable EntrySerializer<MobEffectInstance> getSerializer() {
        return this;
    }

    @Override
    public Component asFormattedText(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return value.getEffect().getDisplayName();
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return BuiltInRegistries.MOB_EFFECT
                .getResourceKey(value.getEffect())
                .flatMap(BuiltInRegistries.MOB_EFFECT::getHolder)
                .map(Holder::tags)
                .orElse(Stream.of());
    }

    @Override
    public boolean supportReading() {
        return true;
    }

    @Override
    public boolean supportSaving() {
        return true;
    }

    @Override
    public boolean acceptsNull() {
        return false;
    }

    @Override
    public CompoundTag save(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return value.save(new CompoundTag());
    }

    @Override
    public MobEffectInstance read(CompoundTag tag) {
        return MobEffectInstance.load(tag);
    }
}
