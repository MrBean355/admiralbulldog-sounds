package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.bytes.*
import com.github.mrbean355.admiralbulldog.persistence.MAX_VOLUME
import javafx.scene.image.Image
import kotlin.reflect.KClass

/* Dimensions */
const val WINDOW_WIDTH = 300.0
const val PADDING_SMALL = 8.0
const val PADDING_MEDIUM = 16.0
const val TEXT_SIZE_SMALL = 10.0
const val TEXT_SIZE_LARGE = 18.0

/* Main Window */
const val TITLE_MAIN_WINDOW = "AdmiralBulldog"
const val MSG_NOT_CONNECTED = "Waiting to hear from Dota 2"
const val MSG_CONNECTED = "Connected to Dota 2!"
const val DESC_NOT_CONNECTED = "You need to be in a match. Try entering Hero Demo mode."
const val DESC_CONNECTED = "Ready to play sounds during your matches!"
const val ACTION_CHANGE_SOUNDS = "Change sounds"
const val LINK_NEED_HELP = "Need help?"
const val LABEL_APP_VERSION = "Version: %s"
const val URL_NEED_HELP = "https://github.com/MrBean355/admiralbulldog-sounds"
const val MSG_NEW_VERSION = "New version available!"
const val LINK_DOWNLOAD = "Download"
const val URL_DOWNLOAD = "https://github.com/MrBean355/admiralbulldog-sounds/releases"

/* Toggle Sound Bytes */
const val TITLE_TOGGLE_SOUND_BYTES = "Enable sounds"
const val LABEL_VOLUME = "Volume"
const val VOLUME_MAJOR_TICK_UNIT = MAX_VOLUME / 5.0
const val VOLUME_MINOR_TICK_COUNT = 3

/* Choose Sound Files */
const val ACTION_SAVE = "Save"

/* Icons */
fun bulldogIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))

fun playIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("play_arrow_black.png"))
fun settingsIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("settings_black.png"))

fun KClass<out SoundByte>.friendlyName(): String {
    return when (this) {
        OnBountyRunesSpawn::class -> "Bounty runes spawning"
        OnDeath::class -> "Got killed"
        OnDefeat::class -> "Lost the match"
        OnHeal::class -> "Got healed"
        OnKill::class -> "Killed a hero"
        OnMatchStart::class -> "Match starting"
        OnMidasReady::class -> "Midas is ready"
        OnRespawn::class -> "Respawned"
        OnSmoked::class -> "Used Smoke of Deceit"
        OnVictory::class -> "Won the match"
        Periodically::class -> "As time goes on"
        else -> simpleName ?: "Unknown"
    }
}