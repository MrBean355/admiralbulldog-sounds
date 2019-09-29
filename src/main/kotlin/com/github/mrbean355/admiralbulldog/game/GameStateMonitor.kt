package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.bytes.RandomSoundByte
import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.bytes.random
import com.github.mrbean355.admiralbulldog.discord.playSoundOnDiscord
import com.github.mrbean355.admiralbulldog.discord.shouldPlayOnDiscord
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.concurrent.thread
import kotlin.reflect.full.createInstance

private const val GSI_PORT = 12345

/** Receives game state updates from Dota 2. */
fun monitorGameStateUpdates(onNewGameState: (GameState) -> Unit) {
    thread(isDaemon = true) {
        embeddedServer(Netty, GSI_PORT) {
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
                        t.printStackTrace()
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }.start(wait = true)
    }
}

private val soundBytes = mutableListOf<SoundByte>()
private var previousState: GameState? = null

/** Play sound bytes that want to be played. */
private fun processGameState(currentState: GameState) {
    val previousMatchId = previousState?.map?.matchid
    val currentMatchId = currentState.map?.matchid

    // Recreate sound bytes when a new match is entered:
    if (currentMatchId != previousMatchId) {
        previousState = null
        soundBytes.clear()
        soundBytes.addAll(SOUND_BYTE_TYPES.map { it.createInstance() })
    }

    // Play sound bytes that want to be played:
    val localPreviousState = previousState
    if (localPreviousState != null && localPreviousState.hasValidProperties() && currentState.hasValidProperties() && currentState.map?.paused == false) {
        soundBytes.forEach {
            if (it.shouldPlay(localPreviousState, currentState)) {
                if (it is RandomSoundByte) {
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

private fun playSoundForType(soundByte: SoundByte) {
    val choices = ConfigPersistence.getSoundsForType(soundByte::class)
    if (choices.isNotEmpty()) {
        val choice = choices.random()
        if (shouldPlayOnDiscord(soundByte)) {
            playSoundOnDiscord(choice)
        } else {
            choice.play()
        }
    }
}
