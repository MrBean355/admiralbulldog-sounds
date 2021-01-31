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
import kotlin.String

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
