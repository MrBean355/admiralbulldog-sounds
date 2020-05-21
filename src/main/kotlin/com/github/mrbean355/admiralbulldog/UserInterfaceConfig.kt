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
import javafx.scene.image.Image
import kotlin.reflect.KClass

/* Dimensions */
const val WINDOW_WIDTH = 300.0
const val PADDING_SMALL = 8.0
const val PADDING_MEDIUM = 16.0
const val TEXT_SIZE_SMALL = 10.0
const val TEXT_SIZE_LARGE = 18.0

/* Hyperlinks */
const val URL_JAVA_DOWNLOAD = "https://www.java.com/en/download"
const val URL_DISCORD_SERVER_INVITE = "https://discordapp.com/invite/pEV4mW5"
const val URL_PROJECT_WEBSITE = "https://github.com/MrBean355/admiralbulldog-sounds"
const val URL_MOD_INFO = "https://github.com/MrBean355/admiralbulldog-sounds/wiki/Dota-Mod"
const val URL_DISCORD_BOT_INVITE = "https://discordapp.com/api/oauth2/authorize?client_id=602822492695953491&scope=bot&permissions=1"
const val URL_DISCORD_WIKI = "https://github.com/MrBean355/admiralbulldog-sounds/wiki/Discord-Bot"

/* Main Window */
const val TITLE_MAIN_WINDOW = "AdmiralBulldog"
const val HEADER_DISCORD_SOUND = "Discord sound"
const val HEADER_EXCEPTION = "Unexpected error"

/* System Tray */
const val ACTION_SHOW = "Show"
const val ACTION_EXIT = "Exit"
const val TRAY_CAPTION = "Over here!"
const val TRAY_MESSAGE = "I've minimized to the system tray!"

/* Choose Sound Files */
const val TOOLTIP_PLAY_LOCALLY = "Play on your machine"
const val PROMPT_SEARCH = "Search..."
const val ACTION_SAVE = "Save"

/* Discord Bot */
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