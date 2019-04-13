package com.github.mrbean355.plugin

import com.github.mrbean355.GameState
import com.github.mrbean355.SoundEffect
import com.github.mrbean355.plugin.util.oneInThreeChance

/** Random sound effect on kills (sometimes). */
class KillGamePlugin : GamePlugin {

    override fun onGameStateUpdated(previousState: GameState, newState: GameState) {
        if (newState.player!!.kills > previousState.player!!.kills && oneInThreeChance()) {
            listOf(SoundEffect.BLBLBL,
                    SoundEffect.FOURHEAD,
                    SoundEffect.HANDS_UP,
                    SoundEffect.DUMB_SHIT_PLEB,
                    SoundEffect.IM_COMING,
                    SoundEffect.KREYGASM,
                    SoundEffect.LULDOG,
                    SoundEffect.MOTHER_COMES,
                    SoundEffect.OH_MY_GOD,
                    SoundEffect.OMEGA_EZ,
                    SoundEffect.THATS_POWER,
                    SoundEffect.UNLIMITED_POWER).random().play()
        }
    }
}