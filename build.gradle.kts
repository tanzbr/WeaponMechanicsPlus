group = "me.deecaad"
version = "0.1.0"

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

bukkit {
    main = "me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusLoader"
    name = "WeaponMechanicsPlus"
    apiVersion = "1.13"

    authors = listOf("DeeCaaD", "CJCrafter")
    softDepend = listOf("MechanicsCore", "WeaponMechanics")
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsMain")
        credentials {
            username = findProperty("user").toString()
            password = findProperty("pass").toString()
        }
    }

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsAutoDownload")
        credentials {
            username = findProperty("user").toString()
            password = findProperty("pass").toString()
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")

    api("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
    implementation("co.aikar:minecraft-timings:1.0.4")

    compileOnly("me.deecaad:mechanicscore:1.5.1")
    compileOnly("me.deecaad:weaponmechanics:1.11.1")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("me.cjcrafter:mechanicsautodownload:1.2.0")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("WeaponMechanicsPlus-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate ("me.cjcrafter.auto", "me.deecaad.weaponmechanicsplus.lib.auto") {
            include(dependency("me.cjcrafter:mechanicsautodownload"))
        }
        relocate ("co.aikar.timings.lib", "me.deecaad.weaponmechanicsplus.lib.timings") {
            include(dependency("co.aikar:minecraft-timings"))
        }
        relocate ("org.bstats", "me.deecaad.weaponmechanicsplus.lib.bstats") {
            include(dependency("org.bstats:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(8)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

tasks.test {
    useJUnitPlatform()
}