import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    `maven-publish`
}

group = "com.github.devngho"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
}

java {
    withSourcesJar()
}

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

publishing {
    publications {
        create <MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = "diffusion"
            version = project.version as String

            from(components["kotlin"])
        }
    }
}