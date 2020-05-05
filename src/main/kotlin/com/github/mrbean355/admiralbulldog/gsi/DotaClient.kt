package com.github.mrbean355.admiralbulldog.gsi

import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.game.GameState
import com.github.mrbean355.admiralbulldog.ui2.SoundBite
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import tornadofx.Component
import kotlin.random.Random

class DotaClient : Component() {
    private val coroutineScope = CoroutineScope(Default + SupervisorJob())
    private val discordBotRepository = DiscordBotRepository()
    private var server = GsiServer(this::processNewGameState)
    private var previousState: GameState? = null

    init {
        server.start()
    }

    fun stop() {
        coroutineScope.cancel()
        server.stop(gracePeriodMillis = 0, timeoutMillis = 0)
    }

    private fun processNewGameState(newState: GameState) {
        coroutineScope.launch {
            val previousMatchId = previousState?.map?.matchid
            val currentMatchId = newState.map?.matchid

            // Reset sound events when a new match is entered:
            if (currentMatchId != previousMatchId) {
                previousState = null
                SoundTrigger.onEach {
                    it.reset()
                }
            }

            // Play sound bites that want to be played:
            val localPreviousState = previousState
            if (localPreviousState != null && localPreviousState.hasValidProperties() && newState.hasValidProperties() && newState.map?.paused == false) {
                SoundTrigger.onEach {
                    if (AppConfig.triggerEnabledProperty(it).value) {
                        if (it.didHappen(localPreviousState, newState)) {
                            val chance = AppConfig.triggerChanceProperty(it).value / 100.0
                            if (Random.nextDouble() < chance) {
                                val choices = AppConfig.getTriggerSoundBites(it)
                                if (choices.isNotEmpty()) {
                                    val minRate = AppConfig.triggerMinRateProperty(it).value
                                    val maxRate = AppConfig.triggerMaxRateProperty(it).value
                                    val rate = if (minRate != maxRate) Random.nextDouble(from = minRate, until = maxRate) else minRate
                                    playSoundBite(it, choices.random(), rate)
                                }
                            }
                        }
                    }
                }
            }
            previousState = newState
            fire(NewGameStateEvent())
        }
    }

    /** Play [soundBite] through Discord if applicable. Otherwise, play it locally. */
    private fun playSoundBite(soundTrigger: SoundTrigger, soundBite: SoundBite, rate: Double) {
        val discordBotEnabled = AppConfig.discordBotEnabledProperty().get()
        val playThroughDiscord = AppConfig.playThroughDiscordProperty(soundTrigger).get()
        if (discordBotEnabled && playThroughDiscord) {
            coroutineScope.launch {
                if (!discordBotRepository.playSoundNew(soundBite.fileName).isSuccessful()) {
                    soundBite.play(rate)
                }
            }
        } else {
            soundBite.play(rate)
        }
    }
}
