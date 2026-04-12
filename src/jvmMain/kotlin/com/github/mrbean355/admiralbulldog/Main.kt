package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.repo.hostUrl
import javafx.application.Application

/** Program argument to point to a custom host. Points to prod if omitted. */
private const val ARG_HOST_URL = "--host-url"

fun main(args: Array<String>) {
    if (checkJavaVersion() && checkOperatingSystem()) {
        setCustomHostUrl(args)
        Application.launch(DotaApplication::class.java, *args)
    }
}

private fun setCustomHostUrl(args: Array<String>) {
    val hostUrlArg = args.firstOrNull { it.startsWith(ARG_HOST_URL) } ?: return
    hostUrl = hostUrlArg.substringAfterLast('=')
    if (hostUrl.isNotBlank()) {
        println("Using custom host URL: $hostUrl")
    }
}
