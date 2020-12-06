plugins {
    kotlin("jvm")
    id("io.ebean")
    kotlin("kapt")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":qyoga-api"))
    api(project(":backend:qyoga-stdlib"))

    implementation("io.ebean:ebean:12.3.6")
    implementation("io.ebean:persistence-api:2.2.4")
    implementation("io.ebean:ebean-annotation:6.12")
}

kapt {
    generateStubs = true
}