plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val mc_version: String by extra
val jei_version: String by extra
val rei_version: String by extra

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-fabric-api:${jei_version}")
    modRuntimeOnly("mezz.jei:jei-${mc_version}-fabric:${jei_version}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rei_version}")
    // the platform artifacts JiJ cloth-config/basic-math/architectury, so pull the api for those classes.
    // its intermediary-mapped commons would land on the compile classpath as-is and shadow the remapped
    // -fabric variants the platform artifact brings, so keep them out
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:${rei_version}") {
        exclude(group = "me.shedaniel.cloth", module = "cloth-config")
        exclude(group = "dev.architectury", module = "architectury")
    }

    // stylish effects 21.11.3 dropped its fuzs.stylisheffects.api.v1.client package, so there is
    // nothing left to compile the compat against
    // modCompileOnly("maven.modrinth:stylish-effects:BLXq0i5K")
    // modCompileOnly("fuzs.puzzleslib:puzzleslib-fabric:21.11.13")
}
