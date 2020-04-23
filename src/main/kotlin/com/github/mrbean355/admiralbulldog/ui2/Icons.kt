package com.github.mrbean355.admiralbulldog.ui2

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

@Suppress("FunctionName")
fun BulldogIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))

@Suppress("FunctionName")
fun WeirdChampIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("weird_champ.png"))

@Suppress("FunctionName")
fun PoggiesIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("poggies.png"))
