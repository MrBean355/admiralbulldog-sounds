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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier.CONST
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateBuildConfigTask : DefaultTask() {
    var platform: String = ""

    @TaskAction
    fun run() {
        val semver = ClassName("com.vdurmont.semver4j", "Semver")

        FileSpec.builder("com.github.mrbean355.admiralbulldog", "BuildConfig")
                .addProperty(PropertySpec.builder("APP_VERSION", semver)
                        .initializer("Semver(\"${project.version}\")")
                        .build())
                .addProperty(PropertySpec.builder("DISTRIBUTION", STRING, CONST)
                        .initializer("\"$platform\"")
                        .build())
                .build()
                .writeTo(File(project.projectDir, "/src/main/kotlin"))
    }
}