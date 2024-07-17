plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("maven-publish")
}

group = "no.sahito.geokt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {}


kotlin {
    jvmToolchain(11)
    jvm("desktop") {
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {}

    dependencies {
        commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    }

}
