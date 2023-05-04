package net.mehvahdjukaar.jeed;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.apache.commons.lang3.StringUtils;

public class PaintingInfo {

    private final PaintingVariant painting;
    private final Component name;
    private final Component description;

    public PaintingInfo(PaintingVariant painting) {
        ResourceLocation r = Registry.PAINTING_VARIANT.getKey(painting);
        this.description = Component.translatable("jepp.painting.description",
                formatName(r.getNamespace()),
                painting.getWidth(), painting.getHeight());
        String name = r.getPath();

        Component text = Component.translatable(name);
        if (text.getString().equals(name)) text = formatName(name);

        this.name = text;
        this.painting = painting;
    }

    private Component formatName(String name) {
        name = name.replace("_", " ");
        name = StringUtils.capitalize(name);
        return Component.literal(name);
    }

    public Component getDescription() {
        return description;
    }

    public Component getName() {
        return name;
    }

    public PaintingVariant getPainting() {
        return painting;
    }


}