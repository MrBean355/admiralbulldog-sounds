/*
 * Copyright 2024 Michael Johnston
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

import com.github.mrbean355.admiralbulldog.arch.repo.hostUrl
import tornadofx.launch

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

fun main(args: Array<String>) {
    if (checkJavaVersion() && checkOperatingSystem()) {
        setCustomHostUrl(args)
        launch<DotaApplication>()
    }
}

private fun setCustomHostUrl(args: Array<String>) {
    val hostUrlArg = args.firstOrNull { it.startsWith(ARG_HOST_URL) } ?: return
    hostUrl = hostUrlArg.substringAfterLast('=')
    if (hostUrl.isNotBlank()) {
        println("Using custom host URL: $hostUrl")
    }
}
