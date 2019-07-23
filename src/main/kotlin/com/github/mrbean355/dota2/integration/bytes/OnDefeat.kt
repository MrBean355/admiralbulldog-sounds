package com.github.mrbean355.dota2.integration.bytes

import com.github.mrbean355.dota2.integration.GameState
import com.github.mrbean355.dota2.integration.TEAM_NONE
import com.github.mrbean355.dota2.integration.assets.SoundFile

class OnDefeat : SoundByte {
    override val choices = listOf(SoundFile.WEFUCKINGLOST)

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        return current.map!!.win_team != TEAM_NONE && previous.map!!.win_team == TEAM_NONE
                && current.player!!.team_name != current.map.win_team
    }
}