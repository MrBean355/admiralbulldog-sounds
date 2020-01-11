package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.events.OnBountyRunesSpawn
import com.github.mrbean355.admiralbulldog.events.OnDeath
import com.github.mrbean355.admiralbulldog.events.OnDefeat
import com.github.mrbean355.admiralbulldog.events.OnHeal
import com.github.mrbean355.admiralbulldog.events.OnKill
import com.github.mrbean355.admiralbulldog.events.OnMatchStart
import com.github.mrbean355.admiralbulldog.events.OnMidasReady
import com.github.mrbean355.admiralbulldog.events.OnRespawn
import com.github.mrbean355.admiralbulldog.events.OnSmoked
import com.github.mrbean355.admiralbulldog.events.OnVictory
import com.github.mrbean355.admiralbulldog.events.Periodically
import com.github.mrbean355.admiralbulldog.events.SoundEvent
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
const val LINK_DISCORD_COMMUNITY = "Discord community"
const val LINK_PROJECT_WEBSITE = "Website"
const val LABEL_APP_VERSION = "Version: %s"
const val URL_DISCORD_INVITE = "https://discordapp.com/invite/pEV4mW5"
const val URL_PROJECT_WEBSITE = "https://github.com/MrBean355/admiralbulldog-sounds"
const val MSG_SPECIFY_VALID_DOTA_DIR = "You need to specify a valid Dota 2 installation directory to use the app."
const val MSG_REMOVED_SOUNDS = "One or more sounds you were using got removed:\n" +
        "%s\n" +
        "Unfortunately you can't use them any more."
const val MSG_INSTALLER = "Please choose your Dota 2 installation directory."
const val TITLE_INSTALLER = "Choose Dota 2 directory"
const val HEADER_INSTALLER = "Installer"
const val HEADER_UPDATE_AVAILABLE = "Update available!"
const val HEADER_UPDATER = "Updater"
const val HEADER_REMOVED_SOUNDS = "Sounds removed"
const val HEADER_DISCORD_SOUND = "Discord sound"
const val HEADER_EXCEPTION = "Unexpected error"
const val MSG_INSTALLER_SUCCESS = "Successfully installed! Please restart Dota if it's open."

/* System Tray */
const val ACTION_SHOW = "Show"
const val ACTION_EXIT = "Exit"
const val TRAY_CAPTION = "Over here!"
const val TRAY_MESSAGE = "I've minimized to the system tray!"

/* Toggle Sound Bytes */
const val TITLE_TOGGLE_SOUND_EVENTS = "Enable sounds"
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

val KClass<out SoundEvent>.friendlyName: String
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

val KClass<out SoundEvent>.description: String
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