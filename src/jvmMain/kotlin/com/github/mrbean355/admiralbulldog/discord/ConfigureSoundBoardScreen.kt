package com.github.mrbean355.admiralbulldog.discord

import com.github.mrbean355.admiralbulldog.assets.SoundBite
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.sounds.SoundBiteTracker
import tornadofx.Fragment

class ConfigureSoundBoardScreen : Fragment(getString("title_customise_sound_board")) {
    private val tracker = SoundBiteTracker(ConfigPersistence.getSoundBoard())

    override val root = tracker.createUi(this, ::onSaveClicked)

    private fun onSaveClicked(selection: List<SoundBite>) {
        ConfigPersistence.setSoundBoard(selection)
        close()
    }
}