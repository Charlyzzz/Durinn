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
    val http4kVersion = "3.137.1"

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.http4k", "http4k-core", http4kVersion)
    implementation("org.http4k", "http4k-client-apache", http4kVersion)
    implementation("org.http4k", "http4k-format-jackson", http4kVersion)
    implementation("org.http4k", "http4k-serverless-lambda", http4kVersion)
    implementation("io.konform", "konform", "0.1.0")

    testImplementation("junit", "junit", "4.12")
    testImplementation("com.willowtreeapps.assertk", "assertk", "0.10")
    testImplementation("org.http4k", "http4k-testing-hamkrest", http4kVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
