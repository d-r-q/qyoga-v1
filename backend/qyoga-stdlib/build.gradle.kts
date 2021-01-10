plugins {
    kotlin("jvm")
}

val ktor_version: String by System.getProperties()
val logback_version: String by System.getProperties()

dependencies {
    implementation("io.ktor:ktor-server-core:${ktor_version}")

    implementation("org.postgresql:postgresql:42.2.18")
    implementation("io.thorntail:flyway:2.7.0.Final")
    implementation("io.ebean:ebean:12.3.6")

    implementation("ch.qos.logback:logback-classic:${logback_version}")
}