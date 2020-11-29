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
    mainClassName = "io.ktor.server.netty.EngineMain"
}
