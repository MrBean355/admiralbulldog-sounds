import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "2.3.20"
    kotlin("plugin.compose") version "2.3.20"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "com.github.mrbean355"
version = "1.16.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val fxVersion = "15.0.1"
val currentPlatform = (findProperty("platform") as? String) ?: "win"

kotlin {
    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }
    
    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.10.2")
                implementation("com.google.code.gson:gson:2.13.2")
                implementation("org.slf4j:slf4j-simple:2.0.17")
                implementation("com.squareup.retrofit2:retrofit:3.0.0")
                implementation("com.squareup.retrofit2:converter-gson:3.0.0")
                implementation("com.squareup.retrofit2:converter-scalars:3.0.0")
                implementation("com.vdurmont:semver4j:3.1.0")
                implementation("no.tornado:tornadofx:1.7.20")
                implementation("com.github.mrbean355:dota2-gsi:2.5.0")

                implementation("org.openjfx:javafx-base:$fxVersion:$currentPlatform")
                implementation("org.openjfx:javafx-controls:$fxVersion:$currentPlatform")
                implementation("org.openjfx:javafx-graphics:$fxVersion:$currentPlatform")
                implementation("org.openjfx:javafx-media:$fxVersion:$currentPlatform")
            }
        }
        val jvmTest by getting {
            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.mrbean355.admiralbulldog.MainKt"
    }
}

val buildConfigTask = tasks.register<GenerateBuildConfigTask>("generateBuildConfig") {
    platform = currentPlatform
}

val iconsTask = tasks.register<GenerateIconsTask>("generateIcons")

tasks.withType<Jar> {
    archiveBaseName.set("admiralbulldog-sounds-$currentPlatform")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(
        configurations.named("jvmRuntimeClasspath").map { configuration ->
            configuration.map { if (it.isDirectory) it else zipTree(it) }
        }
    )
    manifest {
        attributes("Main-Class" to "com.github.mrbean355.admiralbulldog.MainKt")
    }
}

tasks.named("compileKotlinJvm").configure {
    dependsOn(buildConfigTask, iconsTask)
}