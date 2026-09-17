plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

dependencies {
    modCompileOnly("me.shedaniel:REIPluginCompatibilities-forge-annotations:9.+")
    modCompileOnly("curse.maven:jei-238222:7034701")
    modCompileOnly("curse.maven:emi-580555:5436759")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:16.0.729")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-fabric:16.0.729")

    modCompileOnly("curse.maven:stylish-effects-543661:5093407")
    modCompileOnly("curse.maven:puzzles-lib-495476:5476061")

    modImplementation("curse.maven:architectury-api-419699:5786326")
    modRuntimeOnly("curse.maven:jei-238222:5846878")
    modCompileOnly("curse.maven:emi-580555:6205505")
    modCompileOnly("curse.maven:ftb-library-fabric-438495:6016744")
}
