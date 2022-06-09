import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "com.github.mrbean355"
version = "2.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

val fxVersion = "15.0.1"
val currentPlatform = (findProperty("platform") as? String) ?: "win"

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.6.2")
    implementation("io.ktor:ktor-server-netty:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-serialization-gson:2.0.2")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.vdurmont:semver4j:3.1.0")
    implementation("no.tornado:tornadofx:1.7.20")

    implementation("org.openjfx:javafx-base:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-controls:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-graphics:$fxVersion:$currentPlatform")
    implementation("org.openjfx:javafx-media:$fxVersion:$currentPlatform")

    implementation(compose.desktop.currentOs)

    testImplementation("junit:junit:4.13.2")
}

compose.desktop {
    application {
        mainClass = "com.github.mrbean355.admiralbulldog.MainKt"
    }
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