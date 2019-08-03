package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnDeath : RandomSoundByte {
    override val chance = 0.33f
    override val choices = listOf(
            SoundFile.AWOO,
            SoundFile.BABYRAGE,
            SoundFile.BANTER,
            SoundFile.BULLDOGAAAH,
            SoundFile.BULLDOGAAAH2,
            SoundFile.FUCKRIGHTOFF,
            SoundFile.GODDAMNPEPEGA,
            SoundFile.HELP,
            SoundFile.ICANTBELIEVEIT,
            SoundFile.ITHURTS,
            SoundFile.LOOL4HEAD,
            SoundFile.NOTLIKETHIS,
            SoundFile.OHNO,
            SoundFile.OHNONO,
            SoundFile.OHNONONO,
            SoundFile.OHNONONO07,
            SoundFile.PEPEGA,
            SoundFile.PERMASMASH,
            SoundFile.RUN,
            SoundFile.TP,
            SoundFile.WTFMAN)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.player!!.deaths > previous.player!!.deaths
    }
}