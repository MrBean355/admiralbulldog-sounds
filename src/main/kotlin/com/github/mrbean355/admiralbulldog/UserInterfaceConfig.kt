package com.github.mrbean355.admiralbulldog

import javafx.scene.image.Image

/* Dimensions */
const val WINDOW_WIDTH = 300.0
const val PADDING_SMALL = 8.0
const val PADDING_MEDIUM = 16.0
const val TEXT_SIZE_LARGE = 18.0

/* Main Window */
const val TITLE_MAIN_WINDOW = "AdmiralBulldog"
const val MSG_NOT_CONNECTED = "Waiting to hear from Dota 2"
const val MSG_CONNECTED = "Connected to Dota 2!"
const val DESC_NOT_CONNECTED = "You need to be in a match. Try entering Hero Demo mode."
const val DESC_CONNECTED = "Ready to play sounds during your matches!"
const val ACTION_CHANGE_SOUNDS = "Change sounds"
const val LINK_NEED_HELP = "Need help?"
const val URL_NEED_HELP = "https://github.com/MrBean355/dota2-integration"
const val MSG_NEW_VERSION = "New version available!"
const val LINK_DOWNLOAD = "Download"
const val URL_DOWNLOAD = "https://github.com/MrBean355/dota2-integration/releases"

/* Toggle Sound Bytes */
const val TITLE_TOGGLE_SOUND_BYTES = "Enable sounds"

/* Choose Sound Files */
const val ACTION_SAVE = "Save"

/* Icons */
fun bulldogIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))

fun playIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("play_arrow_black.png"))
fun settingsIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("settings_black.png"))