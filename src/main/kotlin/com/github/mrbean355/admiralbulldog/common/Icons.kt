package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

@Suppress("FunctionName")
fun BulldogIcon(): Image = loadImage("bulldog.jpg")

@Suppress("FunctionName")
fun WeirdChampIcon(): Image = loadImage("weird_champ.png")

@Suppress("FunctionName")
fun PoggiesIcon(): Image = loadImage("poggies.png")

@Suppress("FunctionName")
fun PlayIcon(): Image = loadImage("play_arrow_black.png")

@Suppress("FunctionName")
fun SettingsIcon(): Image = loadImage("settings_black.png")

@Suppress("FunctionName")
fun HelpIcon(): Image = loadImage("help.png")

@Suppress("FunctionName")
fun AddIcon(): Image = loadImage("plus.png")

@Suppress("FunctionName")
fun DeleteIcon(): Image = loadImage("trash.png")

private fun loadImage(name: String) = Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
