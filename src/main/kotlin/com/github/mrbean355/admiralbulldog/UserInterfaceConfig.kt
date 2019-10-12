package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.bytes.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.bytes.OnDeath
import com.github.mrbean355.admiralbulldog.bytes.OnDefeat
import com.github.mrbean355.admiralbulldog.bytes.OnHeal
import com.github.mrbean355.admiralbulldog.bytes.OnKill
import com.github.mrbean355.admiralbulldog.bytes.OnMatchStart
import com.github.mrbean355.admiralbulldog.bytes.OnMidasReady
import com.github.mrbean355.admiralbulldog.bytes.OnRespawn
import com.github.mrbean355.admiralbulldog.bytes.OnSmoked
import com.github.mrbean355.admiralbulldog.bytes.OnVictory
import com.github.mrbean355.admiralbulldog.bytes.Periodically
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.MAX_VOLUME
import javafx.scene.image.Image
import kotlin.reflect.KClass

/* Dimensions */
const val WINDOW_WIDTH = 300.0
const val PADDING_SMALL = 8.0
const val PADDING_MEDIUM = 16.0
const val TEXT_SIZE_SMALL = 10.0
const val TEXT_SIZE_LARGE = 18.0

/* Sync Sound Bytes */
const val TITLE_SYNC_SOUND_BYTES = "Updating"
const val MSG_SYNC_WELCOME = "Welcome!\nChecking for new PlaySounds...\n"
const val ACTION_DONE = "Done"

/* Main Window */
const val TITLE_MAIN_WINDOW = "AdmiralBulldog"
const val MSG_NOT_CONNECTED = "Waiting to hear from Dota 2"
const val MSG_CONNECTED = "Connected to Dota 2!"
const val DESC_NOT_CONNECTED = "You need to be in a match. Try entering Hero Demo mode."
const val DESC_CONNECTED = "Ready to play sounds during your matches!"
const val ACTION_CHANGE_SOUNDS = "Change sounds"
const val ACTION_DISCORD_BOT = "Discord bot"
const val LINK_NEED_HELP = "Need help?"
const val LABEL_APP_VERSION = "Version: %s"
const val URL_NEED_HELP = "https://github.com/MrBean355/admiralbulldog-sounds"
const val MSG_NEW_VERSION = "New version available!"
const val LINK_DOWNLOAD = "Download"
const val URL_DOWNLOAD = "https://github.com/MrBean355/admiralbulldog-sounds/releases"
const val MSG_REMOVED_SOUNDS = "One or more sounds you were using got removed:\n" +
        "%s\n" +
        "Unfortunately you can't use them any more."

/* Toggle Sound Bytes */
const val TITLE_TOGGLE_SOUND_BYTES = "Enable sounds"
const val LABEL_VOLUME = "Volume"
const val VOLUME_MAJOR_TICK_UNIT = MAX_VOLUME / 5.0
const val VOLUME_MINOR_TICK_COUNT = 3

/* Choose Sound Files */
const val LABEL_PLAY_THROUGH_DISCORD = "Play through Discord"
const val LABEL_CONFIGURE_BOT = "Configure the Discord bot from the main screen"
const val PROMPT_SEARCH = "Search..."
const val ACTION_SAVE = "Save"

/* Discord Bot */
const val TITLE_DISCORD_BOT = "Discord bot"
const val LABEL_ENABLE_DISCORD_BOT = "Enable Discord bot"
const val PROMPT_DISCORD_MAGIC_NUMBER = "Your magic number"
const val ACTION_TEST = "Test"
const val LABEL_DISCORD_BOT_HELP = "What is this?"
const val URL_DISCORD_BOT_HELP = "https://github.com/MrBean355/admiralbulldog-sounds/wiki/Discord-Bot"

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

fun KClass<out SoundByte>.description(): String {
    return when (this) {
        OnBountyRunesSpawn::class -> "Plays a sound 15 seconds before bounty runes spawn."
        OnDeath::class -> "Plays a sound when you die (1 in 3 chance)."
        OnDefeat::class -> "Plays a sound when your ancient explodes."
        OnHeal::class -> "Plays a sound when you get healed for at least 200 HP (1 in 3 chance)."
        OnKill::class -> "Plays a sound when you kill an enemy (1 in 3 chance)."
        OnMatchStart::class -> "Plays a sound when the game starts (clock hits 0)."
        OnMidasReady::class -> "Plays a sound when your Hand of Midas comes off cooldown."
        OnRespawn::class -> "Plays a sound when you respawn."
        OnSmoked::class -> "Plays a sound when you are affected by Smoke of Deceit."
        OnVictory::class -> "Plays a sound when the enemy's ancient explodes."
        Periodically::class -> "Plays a sound every 5 - 15 minutes."
        else -> simpleName ?: "Unknown"
    }
}