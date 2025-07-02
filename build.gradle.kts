plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("kapt") version "2.0.21"
}

group = "org.ratifire"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    kapt ("org.mapstruct:mapstruct-processor:1.4.2.Final")
    implementation ("org.mapstruct:mapstruct:1.4.2.Final")
    implementation("software.amazon.awssdk:dynamodb:2.25.27")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.25.27")
    implementation("software.amazon.awssdk:sqs:2.25.27")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("lambda-fat.jar")
        mergeServiceFiles()
    }

    build {
        dependsOn("shadowJar")
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}