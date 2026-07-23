package net.mehvahdjukaar.jeed.plugin.rei;

import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.forge.REIPluginCommon;

/**
 * REI discovers plugins through these annotations on NeoForge, while Fabric uses the mod json entrypoints.
 */
public final class REIPluginsNeoForge {

    @REIPluginClient
    public static class Client extends REIPlugin {
    }

    @REIPluginCommon
    public static class Common extends REICommonPluginImpl {
    }
}
