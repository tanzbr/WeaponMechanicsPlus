import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.cjcrafter"
version = "2.1.1"

plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
}

bukkitPluginYaml {
    main = "com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus"
    name = "WeaponMechanicsPlus"
    apiVersion = "1.13"
    foliaSupported = true

    authors = listOf("DeeCaaD", "CJCrafter")
    softDepend = listOf("MechanicsCore", "WeaponMechanics")
}

repositories {
    mavenCentral()
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/") // MechanicsCore Snapshots
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven(url = "https://mvn.lumine.io/repository/maven-public/") // MythicMobs
    maven(url = "https://repo.jeff-media.com/public/") // SpigotUpdateChecker
}

dependencies {
    // Core Minecraft dependencies
    compileOnly(libs.armorMechanics)
    compileOnly(libs.spigotApi)
    compileOnly(libs.mechanicsCore)
    compileOnly(libs.weaponMechanics)

    // External "hooks" or plugins that we might interact with
    compileOnly(libs.mythicMobs)

    // Shaded dependencies
    compileOnly(libs.adventureApi)
    compileOnly(libs.bstats)
    compileOnly(libs.commandApi)
    compileOnly(libs.commandApiKotlin)
    compileOnly(libs.foliaScheduler)
    compileOnly(libs.spigotUpdateChecker)
}

tasks.shadowJar {
    archiveFileName.set("WeaponMechanicsPlus-${project.version}.jar")

    // the kotlin plugin adds kotlin-stdlib to the classpath, but we don't want it in the shadow jar
    exclude("kotlin/**")
    exclude("META-INF/*.kotlin_module")
    exclude("org/jetbrains/annotations/**")
    exclude("META-INF/annotations/**")

    val libPackage = "me.deecaad.core.lib"

    relocate("org.bstats", "$libPackage.bstats")
    relocate("net.kyori", "$libPackage.kyori")
    relocate("com.jeff_media.updatechecker", "$libPackage.updatechecker")
    relocate("dev.jorel.commandapi", "$libPackage.commandapi")
    relocate("com.cjcrafter.foliascheduler", "$libPackage.scheduler")
    relocate("kotlin.", "$libPackage.kotlin.")
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