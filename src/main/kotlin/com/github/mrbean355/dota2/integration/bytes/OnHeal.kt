package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnHeal : RandomSoundByte {
    override val chance = 0.33f
    override val choices = listOf(SoundFile.EEL)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return previous.hero!!.health > 0F &&
                current.hero!!.max_health == previous.hero.max_health &&
                current.hero.health - previous.hero.health >= 200 &&
                current.hero.health_percent - previous.hero.health_percent > 5
    }
}