plugins {
    kotlin("jvm")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.devngho"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-core:8.5.1")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.10.0")

    implementation(project(":diffusion-core"))
    implementation("com.github.devngho:nplug:0.1-alpha35")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkit {
    name = "diffusion"
    version = "v1.0.0"
    main = "com.github.devngho.diffusion.papermc.Plugin"
    apiVersion = "1.18"
    authors = listOf("ngho")
    depend = listOf("CommandAPI", "NBTAPI")
    libraries = listOf("org.jetbrains.kotlin:kotlin-reflect:1.6.10", "org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
}

tasks {
    runServer {
        minecraftVersion("1.19.2")
    }
}