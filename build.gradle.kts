import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.cjcrafter"
version = "1.4.5"

plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

bukkit {
    main = "com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusLoader"
    name = "WeaponMechanicsPlus"
    apiVersion = "1.13"

    authors = listOf("DeeCaaD", "CJCrafter")
    softDepend = listOf("MechanicsCore", "WeaponMechanics")
}

repositories {
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://repo.maven.apache.org/maven2/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.aikar.co/content/groups/aikar/")
    maven(url = "https://repo.jeff-media.com/public/")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.0.1")
    implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")

    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.cjcrafter:mechanicscore:3.4.1")
    compileOnly("com.cjcrafter:weaponmechanics:3.4.1")
    compileOnly(files(file("lib/ArmorMechanics-3.0.2.jar")))

    // adventure
    compileOnly("net.kyori:adventure-api:4.15.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.15.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.15.0")
}

tasks.shadowJar {
    archiveFileName.set("WeaponMechanicsPlus-${project.version}.jar")

    dependencies {

        relocate ("kotlin.", "com.cjcrafter.weaponmechanicsplus.lib.kotlin.") {
            include(dependency("org.jetbrains.kotlin:"))
        }

        relocate("org.bstats", "com.cjcrafter.weaponmechanicsplus.lib.bstats") {
            include(dependency("org.bstats:"))
        }
        relocate("com.jeff_media", "com.cjcrafter.weaponmechanicsplus.lib") {
            include(dependency("com.jeff_media:"))
        }
    }

    // This doesn't actually include any dependencies, this relocates all references
    // to the mechanics core lib.
    relocate("net.kyori", "me.deecaad.core.lib")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(16)
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}