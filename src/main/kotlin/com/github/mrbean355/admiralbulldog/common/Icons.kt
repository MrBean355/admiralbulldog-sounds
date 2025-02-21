/*
 * Copyright 2025 Michael Johnston
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

@file:Suppress("FunctionName")

package com.github.mrbean355.admiralbulldog.common

import com.github.mrbean355.admiralbulldog.DotaApplication
import javafx.scene.image.Image

fun AddIcon(): Image = loadImage("add.png")

fun BulldogIcon(): Image = loadImage("bulldog.jpg")

fun DeleteIcon(): Image = loadImage("delete.png")

fun GreenDotIcon(): Image = loadImage("green_dot.png")

fun GreyDotIcon(): Image = loadImage("grey_dot.png")

fun HelpIcon(): Image = loadImage("help.png")

fun MonkaGigaIcon(): Image = loadImage("monka_giga.png")

fun MonkaHmmIcon(): Image = loadImage("monka_hmm.png")

fun MonkaSIcon(): Image = loadImage("monka_s.png")

fun PauseChampIcon(): Image = loadImage("pause_champ.png")

fun PlayIcon(): Image = loadImage("play.png")

fun PoggiesIcon(): Image = loadImage("poggies.png")

fun RedDotIcon(): Image = loadImage("red_dot.png")

fun SadKekIcon(): Image = loadImage("sad_kek.png")

fun SettingsIcon(): Image = loadImage("settings.png")

fun YellowDotIcon(): Image = loadImage("yellow_dot.png")

private fun loadImage(name: String): Image =
    Image(DotaApplication::class.java.classLoader.getResourceAsStream(name))
