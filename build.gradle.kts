import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.cjcrafter"
version = "2.0.2"

plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.5"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

bukkit {
    main = "com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusLoader"
    name = "WeaponMechanicsPlus"
    apiVersion = "1.13"
    foliaSupported = true

    authors = listOf("DeeCaaD", "CJCrafter")
    softDepend = listOf("MechanicsCore", "WeaponMechanics")
}

repositories {
    mavenLocal()
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
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.cjcrafter:mechanicscore:4.0.2")
    compileOnly("com.cjcrafter:weaponmechanics:4.0.4")
    compileOnly("com.cjcrafter:foliascheduler:0.6.3")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.7.0")
    compileOnly("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
    compileOnly(files(file("lib/ArmorMechanics-3.0.2.jar")))

    // adventure
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.18.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.18.0")
}

tasks.shadowJar {
    archiveFileName.set("WeaponMechanicsPlus-${project.version}.jar")

    dependencies {
        relocate("org.bstats", "com.cjcrafter.weaponmechanicsplus.lib.bstats") {
            include(dependency("org.bstats:"))
        }
        relocate("com.jeff_media", "com.cjcrafter.weaponmechanicsplus.lib") {
            include(dependency("com.jeff_media:"))
        }
    }

    // This doesn't actually include any dependencies, this relocates all references
    // to the mechanics core lib.
    relocate("kotlin.", "me.deecaad.core.lib.kotlin.")
    relocate("net.kyori", "me.deecaad.core.lib")
    relocate("com.cjcrafter.foliascheduler", "me.deecaad.core.lib.scheduler")
    relocate("dev.jorel.commandapi", "me.deecaad.core.lib.commandapi")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(21)
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
    jvmTarget = "21"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "21"
}