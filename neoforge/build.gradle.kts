plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

dependencies {
    modCompileOnly("me.shedaniel:REIPluginCompatibilities-forge-annotations:9.+")
    modImplementation("curse.maven:jei-238222:7034701")
    modImplementation("curse.maven:emi-580555:5436759")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:16.0.729")

    modCompileOnly("curse.maven:stylish-effects-543661:5093224")
    modCompileOnly("curse.maven:puzzles-lib-495476:5476063")
    modCompileOnly("curse.maven:farmers-delight-398521:5051242")
}
