plugins {
    kotlin("jvm")
    application
}

val ktor_version: String by System.getProperties()
val slf4j_version: String by System.getProperties()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":backend:qyoga-exercises"))
    implementation("io.ktor:ktor-server-core:${ktor_version}")
    implementation("io.ktor:ktor-server-netty:${ktor_version}")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")

}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xopt-in=io.ktor.util.KtorExperimentalAPI")
    }
}