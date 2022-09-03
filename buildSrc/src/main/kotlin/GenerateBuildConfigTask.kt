/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Calendar

open class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    var platform: String = ""

    @get:Input
    val version: Any
        get() = project.version

    @get:OutputFile
    val output: File
        get() = project.file("src/main/kotlin/com/github/mrbean355/admiralbulldog/BuildConfig.kt")

    @TaskAction
    fun run() {
        output.writeText(
            """
            /*
             * Copyright ${Calendar.getInstance().get(Calendar.YEAR)} Michael Johnston
             *
             * Licensed under the Apache License, Version 2.0 (the "License");
             * you may not use this file except in compliance with the License.
             * You may obtain a copy of the License at
             *
             *    https://www.apache.org/licenses/LICENSE-2.0
             *
             * Unless required by applicable law or agreed to in writing, software
             * distributed under the License is distributed on an "AS IS" BASIS,
             * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
             * See the License for the specific language governing permissions and
             * limitations under the License.
             */
            
            package com.github.mrbean355.admiralbulldog
            
            import com.vdurmont.semver4j.Semver
            
            val APP_VERSION: Semver = Semver("$version")
            
            const val DISTRIBUTION: String = "$platform"
            
            """.trimIndent()
        )
    }
}