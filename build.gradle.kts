import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    val minestomVersion = "aebf72de90"
    val mockkVersion = "1.13.3"
    val coroutinesCoreVersion = "1.6.4"

    implementation("com.github.Rushyverse:core:644fc1b6cc") {
        exclude("ch.qos.logback")
    }
    implementation("com.github.Rushyverse:api:feat~npc-SNAPSHOT")
    implementation("com.ibm.icu:icu4j:72.1")
    implementation("com.github.TogAr2:MinestomPvP:bebed02b71")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesCoreVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.Minestom.Minestom:testing:$minestomVersion")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    withType<JavaCompile> {
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${project.name}.jar")
    }
}

application {
    mainClass.set("com.github.rushyverse.PvpboxServerKt")
}