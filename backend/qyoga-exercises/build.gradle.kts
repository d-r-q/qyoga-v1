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
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")

    implementation("org.apache.poi:poi-ooxml:4.1.2")
    implementation("org.apache.poi:ooxml-schemas:1.4")
}

kapt {
    generateStubs = true
}