package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.assets.SoundFile
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.bytes.RandomSoundByte
import com.github.mrbean355.dota2.integration.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.dota2.integration.bytes.SoundByte
import kotlin.reflect.full.createInstance

/** Play sound bytes that want to be played. */
class GameStateMonitor {
    private val soundBytes = mutableListOf<SoundByte>()
    private var previousState: GameState? = null

    init {
        // Welcome!
        SoundFile.BULLDOGHANDSUP.play()
    }

    fun onUpdate(currentState: GameState) {
        val previousMatchId = previousState?.map?.matchid
        val currentMatchId = currentState.map?.matchid

        // Recreate sound bytes when a new match is entered:
        if (currentMatchId != previousMatchId) {
            println("Current match changed: ${previousMatchId ?: "[none]"} -> ${currentMatchId ?: "[none]"}")
            soundBytes.clear()
            soundBytes.addAll(SOUND_BYTE_TYPES.map { it.createInstance() })
        }

        // Play sound bytes that want to be played:
        val previousState = previousState
        if (previousState != null && previousState.hasValidProperties() && currentState.hasValidProperties()) {
            soundBytes.forEach {
                if (it.shouldPlay(previousState, currentState)) {
                    if (it is RandomSoundByte) {
                        if (random.nextFloat() < it.chance) {
                            it.choices.random().play()
                        }
                    } else {
                        it.choices.random().play()
                    }
                }
            }
        }
        this.previousState = currentState
    }
}
