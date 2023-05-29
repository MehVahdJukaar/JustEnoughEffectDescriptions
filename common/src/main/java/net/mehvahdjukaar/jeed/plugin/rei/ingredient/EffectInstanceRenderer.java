package net.mehvahdjukaar.jeed.plugin.rei.ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.BatchedEntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

public class EffectInstanceRenderer extends EffectRenderer implements BatchedEntryRenderer<MobEffectInstance, TextureAtlasSprite> {

    public static final EffectInstanceRenderer INSTANCE = new EffectInstanceRenderer(true);

    public EffectInstanceRenderer(boolean offset) {
        super(offset);
    }


    @Override
    public int getBatchIdentifier(EntryStack<MobEffectInstance> entry, Rectangle bounds, TextureAtlasSprite extraData) {
        return 0;
    }

    @Override
    public void startBatch(EntryStack<MobEffectInstance> entry, TextureAtlasSprite extraData, PoseStack matrices, float delta) {

    }


    @Override
    public void afterBase(EntryStack<MobEffectInstance> entry, TextureAtlasSprite extraData, PoseStack matrices, float delta) {

    }

    @Override
    public void renderOverlay(EntryStack<MobEffectInstance> entry, TextureAtlasSprite extraData, PoseStack matrices, MultiBufferSource.BufferSource immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void endBatch(EntryStack<MobEffectInstance> entry, TextureAtlasSprite extraData, PoseStack matrices, float delta) {

    }

    @Override
    public TextureAtlasSprite getExtraData(EntryStack<MobEffectInstance> entry) {
        MobEffectTextureManager manager = mc.getMobEffectTextures();
        return manager.get(entry.getValue().getEffect());
    }

    @Override
    public void renderBase(EntryStack<MobEffectInstance> entry, TextureAtlasSprite sprite, PoseStack matrices,
                           MultiBufferSource.BufferSource immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {

        matrices.pushPose();
        render(matrices, sprite, bounds.x, bounds.y, bounds.width, bounds.height);
        matrices.popPose();
    }

    @Override
    public void render(EntryStack<MobEffectInstance> entry, PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
        matrices.pushPose();
        render(matrices, entry.getValue(), bounds.x, bounds.y, bounds.width, bounds.height);
        matrices.popPose();
    }

    @Override
    public @Nullable Tooltip getTooltip(EntryStack<MobEffectInstance> entry, TooltipContext context) {
        return Tooltip.create(getTooltipsWithDescription(entry.getValue(), context.getFlag(), false, false));
    }
}
