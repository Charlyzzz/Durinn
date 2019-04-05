import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
}

group = "com.10pines.kotlin-greenhouse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.http4k", "http4k-core", "3.130.0")
    implementation("org.http4k", "http4k-server-jetty", "3.130.0")
    implementation("org.http4k", "http4k-format-jackson", "3.130.0")
    implementation("am.ik.yavi", "yavi", "0.0.23")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}