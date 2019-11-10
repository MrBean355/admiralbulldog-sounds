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
const val WINDOW_WIDTH_LARGE = 450.0
const val PADDING_SMALL = 8.0
const val PADDING_MEDIUM = 16.0
const val TEXT_SIZE_SMALL = 10.0
const val TEXT_SIZE_LARGE = 18.0

/* Sync Sound Bytes */
const val TITLE_SYNC_SOUND_BYTES = "Updating"
const val MSG_SYNC_WELCOME = "Welcome!\nChecking for new PlaySounds...\n"
const val MSG_SYNC_FAILED = "\nCouldn't connect to PlaySounds page.\nCan't get latest sounds."
const val ACTION_DONE = "Done"

/* Main Window */
const val TITLE_MAIN_WINDOW = "AdmiralBulldog"
const val MSG_NOT_CONNECTED = "Waiting to hear from Dota 2..."
const val MSG_CONNECTED = "Connected to Dota 2!"
const val DESC_NOT_CONNECTED = "You need to be in a match. Try entering Hero Demo mode."
const val DESC_CONNECTED = "Ready to play sounds during your matches!"
const val ACTION_CHANGE_SOUNDS = "Change sounds"
const val ACTION_DISCORD_BOT = "Discord bot"
const val LINK_INSTALL = "Install"
const val LINK_NEED_HELP = "Need help?"
const val LABEL_APP_VERSION = "Version: %s"
const val URL_NEED_HELP = "https://github.com/MrBean355/admiralbulldog-sounds"
const val MSG_NEW_VERSION = "New version available!"
const val LINK_DOWNLOAD = "Download"
const val URL_DOWNLOAD = "https://github.com/MrBean355/admiralbulldog-sounds/releases"
const val MSG_REMOVED_SOUNDS = "One or more sounds you were using got removed:\n" +
        "%s\n" +
        "Unfortunately you can't use them any more."
const val MSG_INSTALLER = "Please choose your Dota 2 installation folder"
const val TITLE_INSTALLER = "Choose Dota 2 folder"
const val HEADER_INSTALLER = "Installer"
const val MSG_INSTALLER_WRONG_FOLDER = "This doesn't look like the Dota 2 folder"
const val ACTION_TRY_AGAIN = "Try again"
const val MSG_INSTALLER_CANT_CREATE = "Couldn't create the file: %s"
const val MSG_INSTALLER_SUCCESS = "Successfully installed! Please restart Dota if it's open."

/* System Tray */
const val ACTION_SHOW = "Show"
const val ACTION_EXIT = "Exit"
const val TRAY_CAPTION = "Over here!"
const val TRAY_MESSAGE = "I've minimized to the system tray!"

/* Toggle Sound Bytes */
const val TITLE_TOGGLE_SOUND_BYTES = "Enable sounds"
const val LABEL_VOLUME = "Volume"
const val VOLUME_MAJOR_TICK_UNIT = MAX_VOLUME / 5.0
const val VOLUME_MINOR_TICK_COUNT = 3

/* Choose Sound Files */
const val TOOLTIP_PLAY_LOCALLY = "Play on your machine"
const val PROMPT_SEARCH = "Search..."
const val ACTION_SAVE = "Save"

/* Discord Bot */
const val TITLE_DISCORD_BOT = "Discord bot"
const val LABEL_ENABLE_DISCORD_BOT = "Enable Discord bot"
const val LINK_INVITE_BOT = "Invite bot to server"
const val TITLE_SOUND_BOARD = "Discord sound board"
const val MSG_DISCORD_PLAY_FAILED = "Couldn't play %s through Discord; is the bot in a voice channel?"
const val PROMPT_DISCORD_MAGIC_NUMBER = "Your magic number"
const val LABEL_PLAY_ON_DISCORD = "Play through Discord:"
const val ACTION_SOUND_BOARD = "Sound board"
const val TOOLTIP_PLAY_ON_DISCORD = "Play through Discord"
const val ACTION_CHOOSE_SOUNDS = "Choose sounds"
const val TITLE_CONFIGURE_SOUND_BOARD = "Choose sounds"

/* Icons */
fun bulldogIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("bulldog.jpg"))

fun playIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("play_arrow_black.png"))
fun settingsIcon() = Image(DotaApplication::class.java.classLoader.getResourceAsStream("settings_black.png"))

val KClass<out SoundByte>.friendlyName: String
    get() {
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

val KClass<out SoundByte>.description: String
    get() {
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