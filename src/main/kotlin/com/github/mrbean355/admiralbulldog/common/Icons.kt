package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

@Suppress("FunctionName")
fun BulldogIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))

@Suppress("FunctionName")
fun PlayIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("play_arrow_black.png"))

@Suppress("FunctionName")
fun SettingsIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("settings_black.png"))

@Suppress("FunctionName")
fun WeirdChampIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("weird_champ.png"))

@Suppress("FunctionName")
fun PoggiesIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("poggies.png"))
