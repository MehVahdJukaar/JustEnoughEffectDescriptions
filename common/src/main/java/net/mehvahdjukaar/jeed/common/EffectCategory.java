package net.mehvahdjukaar.jeed.common;

import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.network.chat.Component;

public abstract class EffectCategory {
    public static final int RECIPE_WIDTH = 160;
    public static final int RECIPE_HEIGHT = Jeed.EMI ? 106 : 125;
    public static final int LINE_SPACING = 2;
    public static final int SLOT_W = 19;
    public static final int ROWS = Jeed.EMI ? 1 : 2;
    public static final int MAX_BOX_HEIGHT = (SLOT_W * ROWS) + 2;

    public static final int SLOTS_PER_ROW = 7;

    protected static final int Y_OFFSET = 12;

    protected final Component localizedName = Component.translatable("jeed.category.effect_info");

}
