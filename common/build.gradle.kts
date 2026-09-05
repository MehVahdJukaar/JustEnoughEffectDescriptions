plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val mc_version: String by extra
val jei_version: String by extra
val rei_version: String by extra

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:${rei_version}")
    // for the @REIPluginClient / @REIPluginCommon annotations REI uses for discovery on neoforge
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${rei_version}")
    modCompileOnly("me.shedaniel:REIPluginCompatibilities-forge-annotations:11.0.71")

    // EMI has no 26.1 build yet (latest release targets 1.21.1), so the EMI plugin is disabled
    // modCompileOnly("dev.emi:emi-xplat-intermediary:...")
}
