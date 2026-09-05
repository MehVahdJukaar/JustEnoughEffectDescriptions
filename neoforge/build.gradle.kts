plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val mc_version: String by extra
val jei_version: String by extra
val rei_version: String by extra

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-neoforge-api:${jei_version}")
    modRuntimeOnly("mezz.jei:jei-${mc_version}-neoforge:${jei_version}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${rei_version}")
    // the platform artifacts JiJ cloth-config/basic-math/architectury, so pull the api for those classes
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:${rei_version}")

    // No 26.1 build yet
    // modCompileOnly("curse.maven:stylish-effects-543661:5093224")
}
