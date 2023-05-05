package net.mehvahdjukaar.jeed.common;

import net.minecraft.network.chat.Component;

public abstract class EffectCategory {
    public static final int RECIPE_WIDTH = 160;
    public static final int RECIPE_HEIGHT = 125;
    public static final int LINE_SPACING = 2;
    public static final int EMPTY_LIST_EXTRA_HEIGHT = 80;

    protected static final int Y_OFFSET = 12;

    protected final Component localizedName = Component.translatable("jeed.category.effect_info");

}
