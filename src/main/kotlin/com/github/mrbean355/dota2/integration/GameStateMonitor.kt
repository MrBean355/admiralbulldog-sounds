package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.assets.SoundFileRegistry
import com.github.mrbean355.dota2.integration.assets.play
import com.github.mrbean355.dota2.integration.bytes.RandomSoundByte
import com.github.mrbean355.dota2.integration.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.dota2.integration.bytes.SoundByte
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/** Play sound bytes that want to be played. */
class GameStateMonitor {
    private val soundBytes = mutableListOf<SoundByte>()
    private var previousState: GameState? = null

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
        if (previousState != null && previousState.hasValidProperties() && currentState.hasValidProperties() && currentState.map?.paused == false) {
            soundBytes.forEach {
                if (it.shouldPlay(previousState, currentState)) {
                    if (it is RandomSoundByte) {
                        if (random.nextFloat() < it.chance) {
                            playSound(it::class)
                        }
                    } else {
                        playSound(it::class)
                    }
                }
            }
        }
        this.previousState = currentState
    }

    private fun playSound(key: KClass<out SoundByte>) {
        val choices = SoundFileRegistry.get(key)
        if (choices.isNotEmpty()) {
            choices.random().play()
        }
    }
}
