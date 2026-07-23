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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class EffectInstanceDefinition implements EntryDefinition<MobEffectInstance>, EntrySerializer<MobEffectInstance> {

    @Override
    public void fillCrashReport(CrashReport report, CrashReportCategory category, EntryStack<MobEffectInstance> entry) {
        EntryDefinition.super.fillCrashReport(report, category, entry);
        MobEffect effect = entry.getValue().getEffect().value();
        Component displayName = effect.getDisplayName();
        category.setDetail("Effect", displayName::getString);
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
    public Identifier getIdentifier(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return value.getEffect().unwrapKey().get().identifier();
    }

    @Override
    public boolean isEmpty(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return false;
    }

    @Override
    public MobEffectInstance copy(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), value.getDuration(), value.getAmplifier(), value.isAmbient(),
                value.isVisible(), value.showIcon(), value.hiddenEffect);
    }

    @Override
    public MobEffectInstance normalize(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), 30 * 20, 0, value.isAmbient(),
                value.isVisible(), value.showIcon(), value.hiddenEffect);
    }

    @Override
    public MobEffectInstance wildcard(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), 30 * 20);
    }

    @Override
    public long hash(EntryStack<MobEffectInstance> entry, MobEffectInstance value, ComparisonContext context) {
        var i = value.getEffect().hashCode();
        if (context.isExact()) {
            i = 31 * i + value.getAmplifier();
            i = 31 * i + (value.isAmbient() ? 1 : 0);
        }
        return i;
    }

    @Override
    public boolean equals(MobEffectInstance o1, MobEffectInstance o2, ComparisonContext context) {
        if (o1.getEffect() != o2.getEffect()) return false;
        else if (context.isExact()) {
            if (o1.getAmplifier() != o2.getAmplifier()) return false;
            return o1.isAmbient() == o2.isAmbient();
        }
        return true;
    }

    @Override
    public @Nullable ItemStack cheatsAs(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        ItemStack item = new ItemStack(Items.POTION);
        PotionContents potionContents = new PotionContents(Optional.empty(),
                Optional.of(value.getEffect().value().getColor()),
                Collections.singletonList(normalize(entry, value)), Optional.empty());
        item.set(DataComponents.POTION_CONTENTS, potionContents);
        return item;
    }

    @Override
    public @Nullable EntrySerializer<MobEffectInstance> getSerializer() {
        return this;
    }

    @Override
    public Component asFormattedText(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return value.getEffect().value().getDisplayName();
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(EntryStack<MobEffectInstance> entry, MobEffectInstance value) {
        return value.getEffect().tags();
    }

    @Override
    public boolean acceptsNull() {
        return false;
    }

    @Override
    public Codec<MobEffectInstance> codec() {
        return MobEffectInstance.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> streamCodec() {
        return MobEffectInstance.STREAM_CODEC;
    }
}
