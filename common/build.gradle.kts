plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

dependencies {
    modCompileOnly("me.shedaniel:REIPluginCompatibilities-forge-annotations:9.+")

    modCompileOnly("curse.maven:jei-238222:7034701")
    modCompileOnly("curse.maven:emi-580555:5436759")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:16.0.729")
}
