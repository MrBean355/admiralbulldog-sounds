/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.sonarqube.gradle.SonarQubeTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    id("org.sonarqube") version "3.3"
    jacoco
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.test)
    sourceSets(sourceSets.main.get())
    reports {
        xml.required.set(true)
    }
}

tasks.withType<SonarQubeTask> {
    dependsOn(tasks.named("jacocoTestReport"))
}

sonarqube {
    properties {
        property("sonar.projectKey", "sounds-desktop-app")
        property("sonar.organization", "admiral-bulldog-sounds")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation(platform("org.http4k:http4k-bom:4.10.1.0"))
    implementation("org.http4k:http4k-server-netty")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.http4k:http4k-format-kotlinx-serialization")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
}

compose.desktop {
    application {
        mainClass = "com.github.mrbean355.admiralbulldog.MainKt"
    }
}