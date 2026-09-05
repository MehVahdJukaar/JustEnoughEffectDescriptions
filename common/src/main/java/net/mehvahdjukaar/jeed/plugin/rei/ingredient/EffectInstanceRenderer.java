package net.mehvahdjukaar.jeed.plugin.rei.ingredient;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

public class EffectInstanceRenderer extends EffectRenderer implements EntryRenderer<MobEffectInstance> {

    public static final EffectInstanceRenderer INSTANCE = new EffectInstanceRenderer(true);

    public EffectInstanceRenderer(boolean offset) {
        super(offset);
    }

    @Override
    public void render(EntryStack<MobEffectInstance> entry, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        render(graphics, entry.getValue(), bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public @Nullable Tooltip getTooltip(EntryStack<MobEffectInstance> entry, TooltipContext context) {
        return Tooltip.create(getTooltipsWithDescription(entry.getValue(), context.getFlag(), false, false));
    }
}
