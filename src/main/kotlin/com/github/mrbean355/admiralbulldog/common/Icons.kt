@file:Suppress("FunctionName")

package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

public fun AddIcon(): Image = loadImage("add.png")

public fun BulldogIcon(): Image = loadImage("bulldog.jpg")

public fun DeleteIcon(): Image = loadImage("delete.png")

public fun GreenDotIcon(): Image = loadImage("green_dot.png")

public fun GreyDotIcon(): Image = loadImage("grey_dot.png")

public fun HelpIcon(): Image = loadImage("help.png")

public fun MonkaHmmIcon(): Image = loadImage("monka_hmm.png")

public fun MonkaSIcon(): Image = loadImage("monka_s.png")

public fun PauseChampIcon(): Image = loadImage("pause_champ.png")

public fun PlayIcon(): Image = loadImage("play.png")

public fun PoggiesIcon(): Image = loadImage("poggies.png")

public fun RedDotIcon(): Image = loadImage("red_dot.png")

public fun SadKekIcon(): Image = loadImage("sad_kek.png")

public fun SettingsIcon(): Image = loadImage("settings.png")

public fun YellowDotIcon(): Image = loadImage("yellow_dot.png")

private fun loadImage(name: String): Image =
        Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
