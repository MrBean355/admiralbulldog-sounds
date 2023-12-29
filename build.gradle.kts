import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.9.21"
}

group = "com.github.mrbean355"
version = "1.15.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val fxVersion = "15.0.1"
val currentPlatform = (findProperty("platform") as? String) ?: "win"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.10")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.vdurmont:semver4j:3.1.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("com.github.mrbean355:dota2-gsi:2.4.0")

    implementation("org.openjfx:javafx-base:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-controls:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-graphics:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-media:$fxVersion:$currentPlatform")

    testImplementation("junit:junit:4.13.2")
}

tasks.withType<Jar> {
    archiveBaseName.set("admiralbulldog-sounds-$currentPlatform")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    manifest {
        attributes("Main-Class" to "com.github.mrbean355.admiralbulldog.MainKt")
    }
}

val buildConfigTask = tasks.register<GenerateBuildConfigTask>("generateBuildConfig") {
    platform = currentPlatform
}

val iconsTask = tasks.register<GenerateIconsTask>("generateIcons")

tasks.withType<KotlinCompile> {
    dependsOn(buildConfigTask, iconsTask)
}