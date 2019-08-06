package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.admiralbulldog.DotaApplication
import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.bytes.RandomSoundByte
import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.bytes.random
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
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/** Receives game state updates from Dota 2. */
fun monitorGameStateUpdates(port: Int, onNewGameState: (GameState) -> Unit) {
    embeddedServer(Netty, port) {
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
    }.start(wait = false)
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
                        playSoundForType(it::class)
                    }
                } else {
                    playSoundForType(it::class)
                }
            }
        }
    }
    previousState = currentState
}

private fun playSoundForType(type: KClass<out SoundByte>) {
    val choices = ConfigPersistence.getSoundsForType(type)
    if (choices.isNotEmpty()) {
        playSound(choices.random())
    }
}

private const val VOLUME = 0.20
private val players = CopyOnWriteArrayList<MediaPlayer>()

private fun playSound(path: String) {
    val soundFile = SoundFile.valueOf(path)
    val resource = DotaApplication::class.java.classLoader.getResource(soundFile.path)
    if (resource == null) {
        println("!! Error playing: $path")
        return
    }
    val media = Media(resource.toURI().toString())
    MediaPlayer(media).apply {
        volume = VOLUME
        onEndOfMedia = Runnable {
            dispose()
            players.remove(this)
        }
        // Keep a strong reference to players until they finish.
        // Prevents sounds stopping early due to garbage collection.
        players += this
        play()
    }
}
