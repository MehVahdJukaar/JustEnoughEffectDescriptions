package net.mehvahdjukaar.jeed.common;

import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Comparator;

public final class Constants {
    public static final Identifier EFFECT_BACKGROUND_SMALL_TEXTURE = Identifier.withDefaultNamespace(
            "textures/gui/sprites/hud/effect_background.png");
    public static final Identifier EFFECT_BACKGROUND_SMALL_SPRITE = Identifier.withDefaultNamespace(
            "hud/effect_background");

    public static int RECIPE_WIDTH = Jeed.EMI ? 144 : 160;
    public static int RECIPE_HEIGHT = Jeed.EMI ? 130 : 125;
    public static int LINE_SPACING = 2;
    public static int SLOT_W = 19;
    public static int ROWS = 2;
    public static int MAX_BOX_HEIGHT = (SLOT_W * ROWS) + 2;

    public static int SLOTS_PER_ROW = Jeed.EMI ? 7 : 8;

    public static int Y_OFFSET = 12;

    public static Component LOCALIZED_NAME = Component.translatable("jeed.category.effect_info");

    public static Comparator<Identifier> ID_COMPARATOR = (first, second) -> {
        String secondNamespace = second.getNamespace();
        String firstNamespace = first.getNamespace();
        String mc = "minecraft";
        if (mc.equals(firstNamespace) && !mc.equals(secondNamespace)) {
            return -1;
        } else if (!mc.equals(firstNamespace) && mc.equals(secondNamespace)) {
            return 1;
        } else {
            int pathComparison = firstNamespace.compareTo(secondNamespace);
            if (pathComparison != 0) {
                return pathComparison;
            } else {
                return first.getPath().compareTo(second.getPath());
            }
        }
    };

    public static Comparator<Identifier> NAMESPACE_COMPARATOR = (first, second) -> {
        String firstNamespace = first.getNamespace();
        String secondNamespace = second.getNamespace();
        String mc = "minecraft";

        // If one is "minecraft" and the other isn't, prioritize "minecraft"
        if (mc.equals(firstNamespace) && !mc.equals(secondNamespace)) return -1;
        if (!mc.equals(firstNamespace) && mc.equals(secondNamespace)) return 1;

        // Compare namespaces normally
        int namespaceComparison = firstNamespace.compareTo(secondNamespace);
        if (namespaceComparison != 0) return namespaceComparison;

        // Compare paths to ensure uniqueness
        return first.getPath().compareTo(second.getPath());
    };
}
