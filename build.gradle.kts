plugins {
    id("com.possible-triangle.core")
    id("com.possible-triangle.common") apply false
    id("com.possible-triangle.fabric") apply false
    id("com.possible-triangle.neoforge") apply false
    id("net.mehvahdjukaar.candlelight") version "1.2.4" apply false
}

mod {
    additional.add("mod_description")
    additional.add("mod_credits")
    additional.add("mod_license")
    additional.add("mod_homepage")
    additional.add("mod_authors")
    additional.add("mod_github")
}


subprojects {

    apply(plugin = "com.possible-triangle.core")
    apply(plugin = "net.mehvahdjukaar.candlelight")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly("net.mehvahdjukaar:candlelight:1.2.4")
    }

    repositories {
        nexus()
    }

    upload {
        maven {
            nexus()
        }
        curseforge {
            dependencies {
                optional("jei")
                optional("roughly-enough-items")
                optional("stylish-effects")
            }
        }
        modrinth {
            dependencies {
                optional("jei")
                optional("rei")
                optional("stylish-effects")
            }
        }

        forEach {
            changelog = rootProject.file("changelog.md").readText()
            versionName = "${mod.id.get()}-${mod.version.get()}-${project.name}"
            minecraftVersions.set(listOf("1.21.11"))
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "4000"))
        options.release.set(21)
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        // Standard repositories
        mavenLocal()
        mavenCentral()

        flatDir {
            dirs("mods")
        }

        maven { url = uri("https://jitpack.io") }

        maven { url = uri("https://maven.neoforged.net/releases") }
        maven { url = uri("https://maven.architectury.dev") }
        maven { url = uri("https://maven.parchmentmc.org") }

        maven { url = uri("https://maven.blamejared.com") } // JEI
        maven { url = uri("https://maven.shedaniel.me") } // REI
        maven { url = uri("https://maven.terraformersmc.com/") } // TerraformersMC mods, EMI
        maven { url = uri("https://maven.ladysnake.org/releases") } // Ladysnake mods
        maven { url = uri("https://maven.firstdarkdev.xyz/snapshots") } // FirstDarkDev (snapshots)
        maven { url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven") } // Fuzss' Mod Resources
        maven {
            url = uri("https://www.cursemaven.com")
            isAllowInsecureProtocol = true
        }
        maven { url = uri("https://api.modrinth.com/maven") }
    }
}
