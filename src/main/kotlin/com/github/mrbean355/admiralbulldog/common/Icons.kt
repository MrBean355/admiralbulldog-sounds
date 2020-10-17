@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

fun AddIcon(): Image = loadImage("add.png")

fun BulldogIcon(): Image = loadImage("bulldog.jpg")

fun DeleteIcon(): Image = loadImage("delete.png")

fun GreenDotIcon(): Image = loadImage("green_dot.png")

fun GreyDotIcon(): Image = loadImage("grey_dot.png")

fun HelpIcon(): Image = loadImage("help.png")

fun MonkaHmmIcon(): Image = loadImage("monka_hmm.png")

fun MonkaSIcon(): Image = loadImage("monka_s.png")

fun PauseChampIcon(): Image = loadImage("pause_champ.png")

fun PlayIcon(): Image = loadImage("play.png")

fun PoggiesIcon(): Image = loadImage("poggies.png")

fun RedDotIcon(): Image = loadImage("red_dot.png")

fun SadKekIcon(): Image = loadImage("sad_kek.png")

fun SettingsIcon(): Image = loadImage("settings.png")

fun YellowDotIcon(): Image = loadImage("yellow_dot.png")

private fun loadImage(name: String) = Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
