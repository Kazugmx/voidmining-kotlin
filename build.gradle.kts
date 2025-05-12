import org.gradle.jvm.tasks.Jar

plugins {
    application
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

application{
    mainClass.set("$group.MainKt")
}
val jar by tasks.getting(Jar::class){
    manifest{
        attributes["Main-Class"] = "$group.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}