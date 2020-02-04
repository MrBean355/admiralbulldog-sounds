package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.events.RandomSoundEvent
import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.events.random
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread
import kotlin.reflect.full.createInstance

private val logger = LoggerFactory.getLogger("GameStateMonitor")
private val discordBotRepository by lazy { DiscordBotRepository() }
private val soundEvents = mutableListOf<SoundEvent>()
private var previousState: GameState? = null

/** Receives game state updates from Dota 2. */
fun monitorGameStateUpdates(onNewGameState: (GameState) -> Unit) {
    thread(isDaemon = true) {
        embeddedServer(Netty, ConfigPersistence.getPort()) {
            install(ContentNegotiation) {
                gson()
            }
            routing {
                post {
                    try {
                        val gameState = call.receive<GameState>()
                        processGameState(gameState)
                        onNewGameState(gameState)
                    } catch (t: Throwable) {
                        logger.error("Exception during game state update", t)
                    }
                    call.respond(OK)
                }
            }
        }.start(wait = true)
    }
}

/** Play sound bytes that want to be played. */
private fun processGameState(currentState: GameState) {
    val previousMatchId = previousState?.map?.matchid
    val currentMatchId = currentState.map?.matchid

    // Recreate sound bytes when a new match is entered:
    if (currentMatchId != previousMatchId) {
        previousState = null
        soundEvents.clear()
        soundEvents.addAll(SOUND_EVENT_TYPES.map { it.createInstance() })
    }

    // Play sound bytes that want to be played:
    val localPreviousState = previousState
    if (localPreviousState != null && localPreviousState.hasValidProperties() && currentState.hasValidProperties() && currentState.map?.paused == false) {
        soundEvents.forEach {
            if (it.shouldPlay(localPreviousState, currentState)) {
                if (it is RandomSoundEvent) {
                    if (random.nextFloat() < it.chance) {
                        playSoundForType(it)
                    }
                } else {
                    playSoundForType(it)
                }
            }
        }
    }
    previousState = currentState
}

private fun playSoundForType(soundEvent: SoundEvent) {
    val choices = ConfigPersistence.getSoundsForType(soundEvent::class)
    if (choices.isNotEmpty()) {
        val choice = choices.random()
        if (shouldPlayOnDiscord(soundEvent)) {
            GlobalScope.launch {
                val response = discordBotRepository.playSound(choice)
                if (!response.isSuccessful()) {
                    choice.play()
                }
            }
        } else {
            choice.play()
        }
    }
}

private fun shouldPlayOnDiscord(soundEvent: SoundEvent): Boolean {
    return ConfigPersistence.isUsingDiscordBot() && ConfigPersistence.isPlayedThroughDiscord(soundEvent::class)
}
