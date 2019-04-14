plugins {
    kotlin("jvm") version "1.3.30"
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
    implementation("org.http4k", "http4k-server-jetty", "3.130.0")
    implementation("org.http4k", "http4k-format-jackson", "3.130.0")
    implementation("io.konform:konform:0.1.0")
}
