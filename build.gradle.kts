import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.10pines.kotlin-greenhouse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/konform-kt/konform")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.http4k", "http4k-core", "3.130.0")
    implementation("org.http4k", "http4k-format-jackson", "3.130.0")
    implementation("org.http4k", "http4k-serverless-lambda", "3.133.0")
    implementation("io.konform", "konform", "0.1.0")

    testImplementation("junit", "junit", "4.12")
    testImplementation("com.willowtreeapps.assertk", "assertk", "0.10")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}