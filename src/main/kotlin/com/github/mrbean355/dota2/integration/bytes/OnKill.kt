package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnKill : RandomSoundByte {
    override val chance = 0.33f
    override val choices = listOf(
            SoundFile.`12345DEAD`,
            SoundFile.AREDOINGIT,
            SoundFile.BERRYDISGUSTING,
            SoundFile.BLBLBL,
            SoundFile.BLBLBL2,
            SoundFile.BULLDOG4HEAD,
            SoundFile.BULLDOGHANDSUP,
            SoundFile.DUMBSHITPLEB,
            SoundFile.ELEGIGGLE,
            SoundFile.FEELSBADMAN,
            SoundFile.GODDAMNBABOONS,
            SoundFile.IMCOMING,
            SoundFile.KREYGASM,
            SoundFile.NAMASTE,
            SoundFile.LULDOG,
            SoundFile.LULWJEBAITED,
            SoundFile.MONKAGIGA,
            SoundFile.OHMYGOD,
            SoundFile.OMEGAEZ,
            SoundFile.SEEYA,
            SoundFile.SKADOOSH,
            SoundFile.THATSPOWER,
            SoundFile.UNLIMITEDPOWER,
            SoundFile.WTFF)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.player!!.kills > previous.player!!.kills
    }
}