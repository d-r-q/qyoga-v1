plugins {
    val kotlin_version: String by System.getProperties()

    kotlin("jvm") version kotlin_version apply false
    kotlin("kapt") version kotlin_version apply false
    kotlin("plugin.serialization") version kotlin_version apply false
    id("io.ebean") version "12.3.4" apply false
    id("org.flywaydb.flyway") version "6.4.4" apply false
}

allprojects {
    group = "gyoga"
    version = "21.01.1"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

