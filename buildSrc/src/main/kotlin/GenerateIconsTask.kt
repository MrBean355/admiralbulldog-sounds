/*
 * Copyright 2022 Michael Johnston
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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateIconsTask : DefaultTask() {

    @get:InputDirectory
    val input: File
        get() = project.file("src/main/resources")

    @get:OutputFile
    val output: File
        get() = project.file("src/main/kotlin/com/github/mrbean355/admiralbulldog/common/Icons.kt")

    @TaskAction
    fun run() {
        val icons = input.list().orEmpty()
            .filter { it.endsWith(".png") || it.endsWith(".jpg") }
            .sorted()

        val functions = icons.joinToString(separator = "\n\n            ") {
            "fun ${transform(it)}(): Image = loadImage(\"$it\")"
        }

        output.writeText(
            """
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
            
            @file:Suppress("FunctionName")
            
            package com.github.mrbean355.admiralbulldog.common
            
            import com.github.mrbean355.admiralbulldog.DotaApplication
            import javafx.scene.image.Image
            
            $functions
            
            private fun loadImage(name: String): Image =
                Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
            
            """.trimIndent()
        )
    }

    private fun transform(fileName: String): String = buildString {
        val name = fileName.substringBefore('.').capitalize() + "Icon"
        var i = 0
        var upper = false
        while (i < name.length) {
            if (upper) {
                upper = false
                append(name[i].toUpperCase())
            } else if (name[i] == '_') {
                upper = true
            } else {
                append(name[i])
            }
            ++i
        }
    }
}